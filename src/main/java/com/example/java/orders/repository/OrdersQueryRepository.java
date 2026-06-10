package com.example.java.orders.repository;

import com.example.java.orders.dto.CheckoutItemDto;
import com.example.java.orders.dto.CouponDto;

import java.util.List;

public interface OrdersQueryRepository {

    /**
     * 로그인 회원의 장바구니 중 선택한 cartSeq 목록만 주문/결제 화면용 DTO로 조회한다.
     */
    List<CheckoutItemDto> findCheckoutItemsByMemberCart(Long memberSeq, List<Long> cartSeqList);

    List<CouponDto> findAvailableCouponsByMemberSeq(Long memberSeq);

    CouponDto findAvailableCouponByMemberSeqAndMemberCouponSeq(Long memberSeq, Long memberCouponSeq);
}