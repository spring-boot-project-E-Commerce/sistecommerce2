package com.example.java.groupbuy.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.java.groupbuy.entity.GroupBuy;
import com.example.java.groupbuy.entity.GroupBuyStatus;

public interface GroupBuyRepository extends JpaRepository<GroupBuy, Long> {

    /**
     * 마감 시각이 지난 진행 중(ONGOING) 공구 목록 (마감 처리 스케줄러용).
     *   By Status         → WHERE status = ?       (ONGOING)
     *   And EndAtBefore    → AND end_at < ?         (마감 시각 지남)
     * 스케줄러가 이 목록을 받아 각 공구를 개별 트랜잭션(close)으로 확정/무산 판정한다.
     */
    List<GroupBuy> findByStatusAndEndAtBefore(GroupBuyStatus status, LocalDateTime endAt);
}
