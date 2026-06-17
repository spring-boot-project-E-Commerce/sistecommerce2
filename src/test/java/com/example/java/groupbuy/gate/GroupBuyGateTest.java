package com.example.java.groupbuy.gate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/**
 * 공구 입장 게이트(문지기) 단위 검증 (Redis는 Mockito로 대체).
 *
 * DECR 결과에 따른 통과/거절·보상 분기와 fail-open(미시드/Redis 장애)만 검증하므로
 * 실제 Redis 없이 increment 반환값을 모킹한다.
 */
@ExtendWith(MockitoExtension.class)
class GroupBuyGateTest {

    private static final long OPTION_SEQ = 7L;
    private static final String KEY = "shop:groupbuy:gate:option:7";

    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOps;
    @InjectMocks
    private GroupBuyGate gate;

    @Test
    @DisplayName("남은 자리가 있으면(DECR>=0) 통과시킨다")
    void 자리_있으면_통과() {
        when(redisTemplate.hasKey(KEY)).thenReturn(true);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.increment(KEY, -1)).thenReturn(0L); // 마지막 한 자리

        assertThat(gate.tryAdmit(OPTION_SEQ)).isTrue();
        // 통과면 복구(INCR) 호출이 없어야 한다
        verify(valueOps, never()).increment(KEY, 1);
    }

    @Test
    @DisplayName("정원+대기버퍼를 넘으면(DECR<0) 거절하고 카운터를 도로 올린다")
    void 초과면_거절하고_복구() {
        when(redisTemplate.hasKey(KEY)).thenReturn(true);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.increment(KEY, -1)).thenReturn(-1L); // 이미 가득 참

        assertThat(gate.tryAdmit(OPTION_SEQ)).isFalse();
        // -∞ 드리프트 방지: 깎은 만큼 즉시 복구해야 한다
        verify(valueOps).increment(KEY, 1);
    }

    @Test
    @DisplayName("키가 시드되기 전(미시드)에는 게이트를 적용하지 않는다(fail-open)")
    void 미시드면_fail_open() {
        when(redisTemplate.hasKey(KEY)).thenReturn(false);

        assertThat(gate.tryAdmit(OPTION_SEQ)).isTrue();
        // 미시드 키를 DECR하면 전원 거절(fail-closed)되므로 건드리지 않아야 한다
        verify(valueOps, never()).increment(eq(KEY), any(Long.class));
    }

    @Test
    @DisplayName("Redis 장애 시에도 통과시킨다(fail-open) — 정합성은 DB 락이 보장")
    void Redis_장애면_fail_open() {
        when(redisTemplate.hasKey(KEY)).thenThrow(new RuntimeException("redis down"));

        assertThat(gate.tryAdmit(OPTION_SEQ)).isTrue();
    }

    @Test
    @DisplayName("보상(restore)은 시드된 키에만 토큰을 되돌린다")
    void 보상은_시드된_키에만() {
        when(redisTemplate.hasKey(KEY)).thenReturn(true);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);

        gate.restore(OPTION_SEQ);

        verify(valueOps, times(1)).increment(KEY, 1);
    }

    @Test
    @DisplayName("시드는 음수 남은수를 0으로 막아 절대값으로 SET한다")
    void 시드는_음수를_0으로() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);

        gate.seed(OPTION_SEQ, -5);

        verify(valueOps).set(eq(KEY), eq("0"), any());
    }
}
