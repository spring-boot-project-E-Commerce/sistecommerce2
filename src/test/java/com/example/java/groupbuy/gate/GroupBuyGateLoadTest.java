package com.example.java.groupbuy.gate;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 공구 입장 게이트(문지기) 부하 검증 — 플래시 세일 시나리오.
 *
 * <p>"정원 3,000짜리 인기 공구에 1만 명이 동시에 몰린다"를 그대로 재현한다.
 * 실제 Redis(localhost:6379, docker shop-redis)에 대고 가상 스레드 1만 개가 동시에
 * {@link GroupBuyGate#tryAdmit}를 호출하면, 게이트가:
 * <ul>
 *   <li>정원 + 대기버퍼(= 용량)만큼만 통과시키고,</li>
 *   <li>그 너머는 DB에 닿기 전 인메모리에서 즉시 차단하며,</li>
 *   <li>1만 동시 요청에도 통과 수가 용량을 <b>단 1건도</b> 넘지 않음(원자 카운터)</li>
 * </ul>
 * 을 확인한다. DB·전체 컨텍스트를 띄우지 않아 수 초 내에 끝난다(앞단 게이트의 부하 흡수만 측정).
 * 뒷단 DB 비관적 락의 정합성(점유 ≤ 정원, 초과 0건)은 {@code GroupBuyConcurrencyTest}가 검증한다.
 *
 * <p>전제: docker redis(shop-redis)가 떠 있어야 한다. 없으면 연결 실패로 에러난다.
 */
@DisplayName("공구 게이트 부하 — 1만 동시 요청 차단")
class GroupBuyGateLoadTest {

    /** 동시에 몰리는 요청 수 (플래시 세일). */
    private static final int CONCURRENT_REQUESTS = 10_000;
    /** 옵션 발주 정원(점유 가능 수). */
    private static final int ORDER_QTY = 3_000;
    /**
     * 게이트 용량 = 정원 + 대기버퍼. 대기버퍼 = 정원(GroupBuyGateReconciler.WAITING_BUFFER_MULTIPLIER=1)이므로
     * 용량은 정원의 2배. 이 수만큼만 DB로 통과하고 나머지는 차단된다.
     */
    private static final int CAPACITY = ORDER_QTY * 2;
    /** 테스트 전용 옵션 seq (실데이터와 충돌 방지). */
    private static final long OPTION_SEQ = 999_999L;
    private static final String GATE_KEY = "shop:groupbuy:gate:option:" + OPTION_SEQ;

    private static LettuceConnectionFactory connectionFactory;
    private static StringRedisTemplate redisTemplate;
    private static GroupBuyGate gate;

    @BeforeAll
    static void setUp() {
        connectionFactory = new LettuceConnectionFactory("localhost", 6379);
        connectionFactory.afterPropertiesSet();
        connectionFactory.start();
        redisTemplate = new StringRedisTemplate(connectionFactory);
        redisTemplate.afterPropertiesSet();
        gate = new GroupBuyGate(redisTemplate);
    }

    @AfterAll
    static void tearDown() {
        redisTemplate.delete(GATE_KEY);
        connectionFactory.destroy();
    }

    @Test
    @DisplayName("1만 동시 요청 → 용량만큼만 통과, 나머지는 즉시 차단, 과다승인 0")
    void 만명이_몰려도_용량만큼만_통과시킨다() throws InterruptedException {
        // given: 정원 + 대기버퍼(용량)로 게이트 시드
        redisTemplate.delete(GATE_KEY);
        gate.seed(OPTION_SEQ, CAPACITY);

        AtomicInteger admitted = new AtomicInteger(); // 게이트 통과 → DB로 진행
        AtomicInteger rejected = new AtomicInteger(); // 즉시 차단 (DB 미접근)
        CountDownLatch ready = new CountDownLatch(CONCURRENT_REQUESTS);
        CountDownLatch start = new CountDownLatch(1); // 동시 출발 신호

        // when: 가상 스레드 1만 개가 출발선에 모였다가 동시에 게이트를 두드린다
        long elapsedMs;
        try (ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < CONCURRENT_REQUESTS; i++) {
                pool.submit(() -> {
                    ready.countDown();
                    try {
                        start.await();
                        if (gate.tryAdmit(OPTION_SEQ)) {
                            admitted.incrementAndGet();
                        } else {
                            rejected.incrementAndGet();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
            ready.await();                 // 전원 출발선 집결
            long t0 = System.nanoTime();
            start.countDown();             // 동시 출발!
            pool.shutdown();
            boolean finished = pool.awaitTermination(60, TimeUnit.SECONDS);
            elapsedMs = (System.nanoTime() - t0) / 1_000_000;
            assertThat(finished).as("모든 요청이 제한시간 내 처리됨").isTrue();
        }

        int remaining = Integer.parseInt(redisTemplate.opsForValue().get(GATE_KEY));
        printSummary(admitted.get(), rejected.get(), remaining, elapsedMs);

        // then: 용량만큼만 통과, 나머지는 차단, 합은 전체와 일치
        assertThat(admitted.get()).as("통과 = 게이트 용량(정원+대기버퍼)").isEqualTo(CAPACITY);
        assertThat(rejected.get()).as("차단 = 용량 초과분").isEqualTo(CONCURRENT_REQUESTS - CAPACITY);
        assertThat(admitted.get() + rejected.get()).as("유실 없이 전부 처리").isEqualTo(CONCURRENT_REQUESTS);
        // 핵심: 1만 동시에도 통과가 용량을 단 1건도 넘지 않음 → 남은 카운터 정확히 0 (원자성, 과다승인 0)
        assertThat(remaining).as("과다승인 0 (남은 자리 정확히 소진)").isZero();
    }

    private static void printSummary(int admitted, int rejected, int remaining, long elapsedMs) {
        String line = "=".repeat(58);
        System.out.println("\n" + line);
        System.out.println("  공구 입장 게이트 부하 테스트 결과 (플래시 세일)");
        System.out.println(line);
        System.out.printf("  동시 요청        : %,d 명%n", CONCURRENT_REQUESTS);
        System.out.printf("  옵션 정원        : %,d (대기버퍼 포함 용량 %,d)%n", ORDER_QTY, CAPACITY);
        System.out.println("  " + "-".repeat(54));
        System.out.printf("  게이트 통과(→DB) : %,d 건%n", admitted);
        System.out.printf("  즉시 차단(Redis) : %,d 건%n", rejected);
        System.out.printf("  과다승인         : %d 건  (남은 자리 %d)%n", -remaining, remaining);
        System.out.printf("  처리 시간        : %,d ms%n", elapsedMs);
        System.out.println(line + "\n");
    }
}
