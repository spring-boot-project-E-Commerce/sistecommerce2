--배송테이블 지연시간 컬럼 추가
ALTER TABLE delivery ADD (delay_hours number DEFAULT 0 NOT NULL);

--상품 목록 조회 속도 개선을 위해 상품테이블에 대표이미지 컬럼을 추가
ALTER TABLE product ADD (thumbnail_url VARCHAR2(1000));