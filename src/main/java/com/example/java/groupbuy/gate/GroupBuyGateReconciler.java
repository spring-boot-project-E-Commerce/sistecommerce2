package com.example.java.groupbuy.gate;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.java.groupbuy.entity.GroupBuy;
import com.example.java.groupbuy.entity.GroupBuyOptions;
import com.example.java.groupbuy.entity.GroupBuyStatus;
import com.example.java.groupbuy.repository.GroupBuyOptionsRepository;
import com.example.java.groupbuy.repository.GroupBuyRepository;
import com.example.java.groupbuy.repository.WaitingQueueRepository;

import lombok.RequiredArgsConstructor;

/**
 * 게이트 카운터를 DB 권위값으로 맞추는 reconcile 로직 + 게이트 용량 정책 소유자.
 *
 * <p>게이트({@link GroupBuyGate})는 Redis 원자 연산만 하고, "용량을 얼마로 둘지"는 여기서 정한다.
 * 옵션별 게이트 용량 = {@code order_qty}(점유 정원) + 대기버퍼. 대기버퍼는 정원 초과 수요가
 * 무한 대기열로 DB를 덮치지 않게 묶으면서도 기존 대기열 기능을 보존하기 위한 여유분이다.
 * 그래서 통과한 사람만 DB로 가 occupy(정원 내) 또는 대기열 등록(정원~정원+버퍼)을 하고,
 * 그 너머는 게이트가 즉시 거절한다.
 *
 * <p>남은 입장 가능 수 = 용량 - (점유 인원 + 대기 인원). DB가 진실이므로 매분 이 값으로
 * 게이트를 절대 SET(seed)해 취소·만료·승격으로 생긴 드리프트와 Redis 재시작을 자가치유한다.
 */
@Service
@RequiredArgsConstructor
public class GroupBuyGateReconciler {

    /**
     * 대기버퍼 = order_qty × 이 배수. 1이면 "점유 정원만큼 대기 허용"(용량 = order_qty × 2).
     * 플래시 세일에서 DB로 가는 트래픽을 옵션 정원의 상수배로 묶는 손잡이 — 운영하며 조정.
     */
    private static final int WAITING_BUFFER_MULTIPLIER = 1;

    private final GroupBuyRepository groupBuyRepository;
    private final GroupBuyOptionsRepository groupBuyOptionsRepository;
    private final WaitingQueueRepository waitingQueueRepository;
    private final GroupBuyGate gate;

    /** 옵션 게이트 용량 = 점유 정원(order_qty) + 대기버퍼. */
    public int capacity(int orderQty) {
        return orderQty + orderQty * WAITING_BUFFER_MULTIPLIER;
    }

    /** 진행 중(ONGOING) 공구 전체의 게이트를 DB 권위값으로 재시드한다(매분 스케줄러). */
    @Transactional(readOnly = true)
    public void reconcileAllOngoing() {
        List<GroupBuy> ongoing = groupBuyRepository.findByStatusOrderByEndAtAsc(GroupBuyStatus.ONGOING);
        for (GroupBuy gb : ongoing) {
            reconcileOptions(gb.getSeq());
        }
    }

    /**
     * 한 공구의 옵션 게이트를 시드한다. 시작(open) 직후 즉시 시드(60초 무방비 구간 제거)와
     * 매분 reconcile에서 공통으로 쓴다.
     *
     * @param groupBuySeq 대상 공구 seq
     */
    @Transactional(readOnly = true)
    public void reconcileOptions(Long groupBuySeq) {
        List<GroupBuyOptions> options = groupBuyOptionsRepository.findByGroupBuySeq(groupBuySeq);
        for (GroupBuyOptions option : options) {
            long waiting = waitingQueueRepository.countByGroupBuyOptionsSeq(option.getSeq());
            // 남은 입장 가능 수 = 용량 - (점유 + 대기). 음수는 게이트가 0으로 막는다.
            int remaining = capacity(option.getOrderQty()) - option.getOccupiedCount() - (int) waiting;
            gate.seed(option.getSeq(), remaining);
        }
    }
}
