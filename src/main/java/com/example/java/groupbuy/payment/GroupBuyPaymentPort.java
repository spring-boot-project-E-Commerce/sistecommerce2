package com.example.java.groupbuy.payment;

/**
 * 공구 참여 결제 경계(포트).
 * 실제 PG 결제 + orders/order_item/payment 생성은 결제팀 담당이라, 공구 참여 로직은 이 포트 뒤로 분리한다.
 * 현재는 {@link StubPaymentAdapter}(항상 성공)로 대체하고, 추후 결제팀 구현체를 주입한다.
 */
public interface GroupBuyPaymentPort {

    /**
     * 공구 참여/승격 결제 + 주문 생성.
     * 결제팀 구현체가 PG 승인과 orders(order_status=결제완료) + order_item(participation_seq 채움)
     * + payment 생성을 한 트랜잭션에서 처리한다.
     * 실패 시 예외를 던져 호출측 트랜잭션(점유/참여 INSERT)을 함께 롤백시킨다.
     *
     * @param command 결제 요청 (구매 시점 가격 스냅샷 포함)
     */
    void pay(GroupBuyPaymentCommand command);

    /**
     * 공구 참여 환불 (취소/무산). participationSeq 로 원주문(order_item.participation_seq)을 찾아
     * PG 결제취소 + 환불 처리한다 (payment.status=환불완료, orders.order_status=주문취소).
     *
     * <p>★ 결정(2026-06-11): 환불액 = 구매 시점 order_item.final_price 스냅샷 그대로.
     * 공구는 1인1상품·수량1·전액환불(부분환불/쿠폰/핫딜 없음)이므로 '할인율 역산' 로직이 필요 없다.
     * group_buy 를 재조회해 금액을 다시 계산하지 말 것.
     *
     * <p>멱등: 호출측(서비스)이 participation 상태로 멱등을 보장하지만, 결제팀 구현체도 이미 환불된 건이면
     * 실제 PG 취소는 1회만 수행해야 한다 (NFR-004).
     *
     * @param participationSeq 환불 대상 공구 참여 seq
     */
    void refund(Long participationSeq);
}
