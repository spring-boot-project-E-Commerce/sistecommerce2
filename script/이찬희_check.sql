-- 발주 테이블 (purchase_order)
ALTER TABLE purchase_order
ADD (
    CONSTRAINT chk_purchase_order_status
    CHECK (status IN ('발주요청', '입고완료', '물품불량', '입고지연', '지연입고')),

    CONSTRAINT chk_purchase_order_quantity
    CHECK (quantity >= 1),

    CONSTRAINT chk_purchase_order_type
    CHECK (type IN ('일반', '공동구매'))
);

-- 재고이력 테이블 (stock_history)
ALTER TABLE stock_history
ADD (
    CONSTRAINT chk_stock_history_type
        CHECK (type IN ('IN', 'OUT')),

    CONSTRAINT chk_stock_history_quantity
        CHECK (quantity >= 1),

    CONSTRAINT chk_stock_history_before_stock
        CHECK (before_stock >= 0),

    CONSTRAINT chk_stock_history_after_stock
        CHECK (after_stock >= 0),

    CONSTRAINT chk_stock_history_source_type
        CHECK (source_type IN ('주문', '발주', '반품', '관리자'))
);

-- 판매처 테이블 (seller)
ALTER TABLE seller
ADD (
    CONSTRAINT chk_seller_supply_rate
        CHECK (supply_rate BETWEEN 1 AND 100)
);
