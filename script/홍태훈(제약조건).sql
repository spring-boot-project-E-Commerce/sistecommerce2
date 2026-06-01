-- 1. cart 테이블: 중복 상품 담기 방지
ALTER TABLE cart ADD CONSTRAINT UQ_CART_MEMBER_OPTION UNIQUE (member_seq, options_seq);

-- 2. product_wish 테이블: 중복 찜하기 방지
ALTER TABLE product_wish ADD CONSTRAINT UQ_WISH_MEMBER_OPTION UNIQUE (member_seq, options_seq);

-- 3. coupon 테이블: 만료일 가상 컬럼(Virtual) 추가
ALTER TABLE coupon ADD "expire_date" date GENERATED ALWAYS AS (start_date + valid_days) VIRTUAL;

-- 4. coupon 테이블: (할인가격, 할인율) 둘 중 하나 필수 체크
ALTER TABLE coupon ADD CONSTRAINT CHK_COUPON_DISCOUNT CHECK (discount_rate IS NOT NULL OR discount_price IS NOT NULL);

-- 5. hot_deal 테이블: (할인가격, 할인율) 둘 중 하나 필수 체크
ALTER TABLE hot_deal ADD CONSTRAINT CHK_HOTDEAL_DISCOUNT CHECK (discount_rate IS NOT NULL OR discount_price IS NOT NULL);