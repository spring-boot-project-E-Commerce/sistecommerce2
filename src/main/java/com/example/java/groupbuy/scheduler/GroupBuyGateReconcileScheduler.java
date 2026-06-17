package com.example.java.groupbuy.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.java.groupbuy.gate.GroupBuyGateReconciler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 공구 입장 게이트(문지기) 재동기화 스케줄러.
 *
 * 매분 진행 중(ONGOING) 공구의 옵션 게이트를 DB 권위값으로 다시 시드한다.
 * 참여 취소·결제기한 만료·대기열 승격으로 옵션의 점유/대기 인원이 바뀌면 게이트가
 * 드리프트하는데, 이 값이 DB 기준으로 보정된다(Redis 재시작 시에도 자가치유).
 *
 * 게이트는 근사 부하흡수일 뿐 최종 정합성은 DB 비관적 락이 보장하므로,
 * reconcile 사이 1분 동안의 미세 드리프트는 허용한다.
 *
 * @EnableScheduling은 ShopApplication / SchedulerConfig에 이미 활성화돼 있다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GroupBuyGateReconcileScheduler {

    private final GroupBuyGateReconciler gateReconciler;

    /** 매분 30초 실행. (open/close 스케줄러는 0초라 같은 틱에 몰리지 않게 30초로 비킨다.) */
    @Scheduled(cron = "30 * * * * *")
    public void reconcileGates() {
        try {
            gateReconciler.reconcileAllOngoing();
        } catch (Exception e) {
            // reconcile 실패는 admit fail-open + 다음 틱 재시도로 흡수되므로 로그만 남긴다 (NFR-006).
            log.error("[공구 게이트] reconcile 실패", e);
        }
    }
}
