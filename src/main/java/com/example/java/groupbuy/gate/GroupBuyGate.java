package com.example.java.groupbuy.gate;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 공구 참여 입장 게이트(문지기) — 플래시 세일 부하 흡수용 Redis 원자 카운터.
 *
 * <p><b>왜 필요한가.</b> {@code GroupBuyService.participate}는 분기 전에 무조건
 * 옵션 행에 비관적 락(SELECT ... FOR UPDATE)을 건다. 1만 명이 정원 3천짜리 공구에
 * 몰리면 1만 요청이 같은 hot row 락 하나에 줄서며 직렬화되고, 각자 DB 커넥션을 쥔 채
 * 대기해 HikariCP 풀이 고갈된다 → 공구뿐 아니라 사이트 전체가 다운된다.
 * 이 게이트는 "떨어질 다수"를 DB 락에 도달하기 전에 인메모리 카운터로 즉시 걸러
 * DB로 가는 트래픽을 용량 근처의 상수로 묶는다(2단 방어의 앞단).
 *
 * <p><b>권위는 DB.</b> 이 카운터는 정확한 재고가 아니라 <i>근사 admission</i>이다.
 * 최종 정합성(NFR-001 점유 ≤ 정원)은 뒷단의 DB 비관적 락이 보장한다. 그래서 카운터가
 * 약간 흐트러져도(취소/만료로 자리가 바뀌어도) 안전하며, {@link GroupBuyGateReconciler}가
 * 매분 DB 권위값으로 다시 맞춘다. Redis 장애 시에도 <b>fail-open</b>(게이트 우회 → DB 락만으로
 * 정상, 느려도 정확)으로 동작한다.
 *
 * <p>이 클래스는 Redis 원자 연산만 담당하고, "용량을 얼마로 둘지"의 정책은
 * {@link GroupBuyGateReconciler}가 소유한다(관심사 분리).
 *
 * <p>주입 주의: 프로젝트에 lombok.config가 없어 {@code @RequiredArgsConstructor}가
 * {@code @Qualifier}를 생성자로 옮기지 못한다. INCR/DECR만 쓰므로 Spring Boot 기본
 * {@link StringRedisTemplate}을 주입한다(rate limiter와 동일 패턴).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GroupBuyGate {

    /** Redis 키 접두사 (네임스페이스 shop:* 컨벤션). 옵션 단위(점유 단위와 일치). */
    private static final String KEY_PREFIX = "shop:groupbuy:gate:option:";

    /**
     * 시드 키 TTL. reconcile(매분)이 갱신하므로 평상시엔 만료되지 않고,
     * 공구가 마감(ONGOING 이탈)되면 reconcile이 더는 손대지 않아 이 시간 뒤 자동 정리된다.
     * reconcile이 한동안 실패해 만료되면 admit이 fail-open으로 떨어진다(DB가 방어).
     */
    private static final Duration TTL = Duration.ofMinutes(10);

    private final StringRedisTemplate redisTemplate;

    /**
     * 입장 1건을 시도한다. 카운터를 1 줄이고(DECR) 통과 여부를 반환한다.
     *
     * <ul>
     *   <li>키 없음(미시드/Redis 플러시) → {@code true}(fail-open). 시작 직후엔 OpenScheduler가
     *       시드하고 매분 reconcile이 시드하므로 정상 운영 중 이 경로는 드물다.</li>
     *   <li>DECR 결과 ≥ 0 → {@code true}(입장 허용). 통과자는 DB로 가서 occupy 또는 대기열 등록.</li>
     *   <li>DECR 결과 &lt; 0 → 카운터를 즉시 복구(INCR)하고 {@code false}(정원+대기버퍼 초과 → 거절).</li>
     * </ul>
     *
     * @param optionSeq group_buy_options.seq
     * @return true면 입장 허용(DB로 진행), false면 혼잡으로 거절
     */
    public boolean tryAdmit(long optionSeq) {
        String key = KEY_PREFIX + optionSeq;
        try {
            // 미시드 키를 DECR하면 0→-1이 되어 전원 거절(fail-closed)될 수 있다.
            // 시드 전에는 게이트를 적용하지 않는다(fail-open) — 안전은 DB 락이 보장.
            Boolean exists = redisTemplate.hasKey(key);
            if (exists == null || !exists) {
                return true;
            }
            Long remaining = redisTemplate.opsForValue().increment(key, -1);
            if (remaining == null) {
                return true; // Redis 일시 이상 → fail-open
            }
            if (remaining >= 0) {
                return true; // 입장 허용
            }
            // 음수로 내려감 = 용량 초과. 바닥을 도로 올려 -∞ 드리프트를 막고 거절한다.
            redisTemplate.opsForValue().increment(key, 1);
            return false;
        } catch (RuntimeException e) {
            // Redis 장애 → 게이트 우회(fail-open). DB 비관적 락이 최종 방어선이므로 정합성은 유지된다.
            log.warn("[공구 게이트] Redis 접근 실패 → fail-open. optionSeq={}", optionSeq, e);
            return true;
        }
    }

    /**
     * 토큰 1개를 반납(INCR)한다. {@link #tryAdmit}로 통과했지만 뒤따른 DB 처리가
     * 예외로 실패해(중복 참여, 검증 실패 등) 실제 점유/대기 등록을 못 한 경우 보상용.
     *
     * <p>보상이 약간 어긋나도(예: fail-open 통과분을 반납) 매분 reconcile이 DB 권위값으로
     * 절대 보정하므로 누적 드리프트는 생기지 않는다.
     */
    public void restore(long optionSeq) {
        String key = KEY_PREFIX + optionSeq;
        try {
            // 미시드 키엔 굳이 만들지 않는다(다음 reconcile이 시드).
            if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
                redisTemplate.opsForValue().increment(key, 1);
            }
        } catch (RuntimeException e) {
            log.warn("[공구 게이트] 토큰 반납 실패(무시, reconcile이 보정). optionSeq={}", optionSeq, e);
        }
    }

    /**
     * 옵션 게이트를 남은 입장 가능 수로 시드(절대값 SET)한다. 시작(open) 직후·매분 reconcile에서 호출.
     * 절대값을 덮어쓰므로 그 사이 진행된 DECR을 무시하고 DB 권위값으로 카운터를 다시 맞춘다
     * (in-flight DECR과의 경합으로 1분 내 미세 드리프트가 있을 수 있으나 DB 락이 최종 방어).
     *
     * @param optionSeq group_buy_options.seq
     * @param remaining 남은 입장 가능 수(0 이상). 음수면 0으로 막아 음수 시드를 방지.
     */
    public void seed(long optionSeq, int remaining) {
        String key = KEY_PREFIX + optionSeq;
        int value = Math.max(remaining, 0);
        try {
            redisTemplate.opsForValue().set(key, Integer.toString(value), TTL);
        } catch (RuntimeException e) {
            log.warn("[공구 게이트] 시드 실패(무시, admit fail-open). optionSeq={}", optionSeq, e);
        }
    }
}
