-- ==========================================================
-- 시연용 공동구매(group_buy) 더미데이터 — 작성: 황윤재
-- 목적: 발표 시연 — 진행중/예정 공구 + 참여 인원·대기열을 실제 데이터로 채움
--   · 기본 참가(자리 넉넉) · 대기열 진입+승격 · 매진 옵션(대기열 채움) · 마감 임박 · 시작 전(예정)
-- 방식: 카운트용 '진짜' 회원 70명을 만들고(로그인/결제용 아님), 거기에 participation/waiting_queue를 붙임
--       → FK 무결성 유지(가짜 member_seq 안 씀). occupied_count는 활성 참여 수로 동기화(NFR-003).
-- 전제: dummy_data.sql 선행(seller '판매처 1', category 헤드폰·마우스·노트북·이어폰·운동화 존재)
--       이미지 파일은 static/src/images/product/ 에 존재(기존 더미 이미지 재사용)
-- 최대 인원(max_count)=300 (옵션 order_qty 합 = max_count)
-- 타임존: KST 벽시계 — SYSTIMESTAMP 직접 금지, AT TIME ZONE 'Asia/Seoul' 캐스팅.
-- 주의: dev 프로파일이 보는 클라우드 Oracle에 들어갑니다(팀 공용일 수 있음).
-- ==========================================================

-- ==========================================================
-- ★ 재실행 안전 처리 (ORA-00001 무결성 위반 방지) — 반드시 메인 INSERT 전에 실행
--   원인: 시퀀스가 테이블 실제 max(seq)보다 뒤처져 NEXTVAL이 '이미 있는 PK'를 다시 뱉음.
--        (또는 재실행 시 demo_buyer 회원 username 중복.)
--   해결: ① 이전 [시연]/demo_buyer 더미 정리 → ② 시퀀스를 max(seq)+1로 보정.
-- ==========================================================

-- ① 이전 시연 더미 정리: '[시연]' 상품과 'demo_buyer_' 회원만 정확히 제거 (FK 자식→부모 순서)
BEGIN
    DELETE FROM participation WHERE group_buy_seq IN (
        SELECT gb.seq FROM group_buy gb JOIN product p ON p.seq = gb.product_seq
        WHERE p.product_name LIKE '[시연]%');
    DELETE FROM waiting_queue WHERE group_buy_seq IN (
        SELECT gb.seq FROM group_buy gb JOIN product p ON p.seq = gb.product_seq
        WHERE p.product_name LIKE '[시연]%');
    DELETE FROM group_buy_options WHERE group_buy_seq IN (
        SELECT gb.seq FROM group_buy gb JOIN product p ON p.seq = gb.product_seq
        WHERE p.product_name LIKE '[시연]%');
    DELETE FROM group_buy WHERE product_seq IN (
        SELECT seq FROM product WHERE product_name LIKE '[시연]%');
    DELETE FROM product_image WHERE product_seq IN (
        SELECT seq FROM product WHERE product_name LIKE '[시연]%');
    DELETE FROM options WHERE product_seq IN (
        SELECT seq FROM product WHERE product_name LIKE '[시연]%');
    DELETE FROM product WHERE product_name LIKE '[시연]%';
    -- demo_buyer 회원의 잔여 참여/대기 제거 후 회원 삭제
    DELETE FROM participation WHERE member_seq IN (
        SELECT seq FROM member WHERE username LIKE 'demo\_buyer\_%' ESCAPE '\');
    DELETE FROM waiting_queue WHERE member_seq IN (
        SELECT seq FROM member WHERE username LIKE 'demo\_buyer\_%' ESCAPE '\');
    DELETE FROM member WHERE username LIKE 'demo\_buyer\_%' ESCAPE '\';
    COMMIT;
END;
/

-- ② 시퀀스 보정: 정리 후 각 테이블 max(seq)+1로 → NEXTVAL이 기존 PK와 겹치지 않게
DECLARE
    v_max NUMBER;
    PROCEDURE fix(p_tab VARCHAR2, p_seq VARCHAR2) IS
    BEGIN
        EXECUTE IMMEDIATE 'SELECT NVL(MAX(seq),0)+1 FROM ' || p_tab INTO v_max;
        EXECUTE IMMEDIATE 'ALTER SEQUENCE ' || p_seq || ' RESTART START WITH ' || v_max;
    END;
BEGIN
    fix('product',           'product_seq');
    fix('options',           'options_seq');
    fix('product_image',     'product_image_seq');
    fix('group_buy',         'group_buy_seq');
    fix('group_buy_options', 'group_buy_options_seq');
    fix('member',            'member_seq');
    fix('participation',     'participation_seq');
    fix('waiting_queue',     'waiting_queue_seq');
END;
/

DECLARE
    TYPE num_tab IS TABLE OF NUMBER INDEX BY PLS_INTEGER;
    v_mem   num_tab;                 -- 데모 회원 seq 풀 (1..70)
    v_seller NUMBER;
    v_cat    NUMBER;
    v_prod   NUMBER;
    v_now    TIMESTAMP := CAST(SYSTIMESTAMP AT TIME ZONE 'Asia/Seoul' AS TIMESTAMP);

    -- 공구 seq
    v_gb1 NUMBER; v_gb2 NUMBER; v_gb3 NUMBER; v_gb4 NUMBER; v_gb5 NUMBER;
    -- 공구옵션 seq
    v_g1o1 NUMBER;
    v_g2o1 NUMBER; v_g2o2 NUMBER;
    v_g3o1 NUMBER; v_g3o2 NUMBER;
    v_g4o1 NUMBER;
    v_g5o1 NUMBER; v_g5o2 NUMBER; v_g5o3 NUMBER;
    -- 옵션(상품옵션) seq
    v_opt1 NUMBER; v_opt2 NUMBER; v_opt3 NUMBER;
BEGIN
    SELECT seq INTO v_seller FROM seller WHERE name = '판매처 1' AND ROWNUM = 1;

    -- =========================================================
    -- 0) 카운트용 데모 회원 70명 (로그인/결제용 아님 — 참여·대기열 FK 채우기용)
    --    필수 컬럼만: username/nickname(UNIQUE), status=1, role, login_type. password는 NULL.
    -- =========================================================
    FOR i IN 1..70 LOOP
        INSERT INTO member (seq, username, nickname, status, role, login_type)
        VALUES (member_seq.NEXTVAL, 'demo_buyer_' || i, '데모구매자' || i, 1, 'ROLE_USER', 'LOCAL')
        RETURNING seq INTO v_mem(i);
    END LOOP;

    -- =========================================================
    -- 1) [시연] 포칼 유토피아 헤드폰 | 헤드폰 | 500만→300만 | ONGOING
    --    기본 참가(자리 넉넉) + 참여 25명
    -- =========================================================
    SELECT seq INTO v_cat FROM category WHERE category_name = '헤드폰' AND depth_level = 2 AND ROWNUM = 1;
    INSERT INTO product (seq, seller_seq, category_seq, product_name, price, content, approval_status)
    VALUES (product_seq.NEXTVAL, v_seller, v_cat, '[시연] 포칼 유토피아 헤드폰', 5000000,
            '포칼 플래그십 오픈형 레퍼런스 헤드폰. 베릴륨 M자형 돔 드라이버.', 'APPROVED')
    RETURNING seq INTO v_prod;
    INSERT INTO options (seq, product_seq, color, stock, safety_stock, additional_price)
    VALUES (options_seq.NEXTVAL, v_prod, '블랙', 500, 10, 0) RETURNING seq INTO v_opt1;
    INSERT INTO product_image (seq, product_seq, image_url, public_id, thumbnail_yn)
    VALUES (product_image_seq.NEXTVAL, v_prod, '/src/images/product/New_Utopia_34.jpg', 'New_Utopia_34', 'Y');
    INSERT INTO group_buy (seq, product_seq, start_at, end_at, created_at, min_count, max_count, original_price, final_price, status)
    VALUES (group_buy_seq.NEXTVAL, v_prod, v_now - INTERVAL '1' DAY, v_now + INTERVAL '7' DAY, v_now,
            5, 300, 5000000, 3000000, 'ONGOING') RETURNING seq INTO v_gb1;
    INSERT INTO group_buy_options (seq, group_buy_seq, options_seq, order_qty, occupied_count)
    VALUES (group_buy_options_seq.NEXTVAL, v_gb1, v_opt1, 300, 0) RETURNING seq INTO v_g1o1;
    -- 참여 25명 (회원 1..25)
    FOR i IN 1..25 LOOP
        INSERT INTO participation (seq, group_buy_seq, group_buy_options_seq, member_seq, status, created_at)
        VALUES (participation_seq.NEXTVAL, v_gb1, v_g1o1, v_mem(i), 'PARTICIPATING', v_now);
    END LOOP;

    -- =========================================================
    -- 2) [시연] 로지텍 지슈라 마우스 | 마우스 | 20만→14만 | ONGOING
    --    대기열 진입+승격 라이브용: 블랙 정원 1 + 점유 0 (참여자 없음). 화이트는 참여 12명.
    -- =========================================================
    SELECT seq INTO v_cat FROM category WHERE category_name = '마우스' AND depth_level = 2 AND ROWNUM = 1;
    INSERT INTO product (seq, seller_seq, category_seq, product_name, price, content, approval_status)
    VALUES (product_seq.NEXTVAL, v_seller, v_cat, '[시연] 로지텍 지슈라 마우스', 200000,
            'PRO X SUPERLIGHT 2 무선 게이밍 마우스. 60g 초경량.', 'APPROVED')
    RETURNING seq INTO v_prod;
    INSERT INTO options (seq, product_seq, color, stock, safety_stock, additional_price)
    VALUES (options_seq.NEXTVAL, v_prod, '블랙', 500, 10, 0) RETURNING seq INTO v_opt1;
    INSERT INTO options (seq, product_seq, color, stock, safety_stock, additional_price)
    VALUES (options_seq.NEXTVAL, v_prod, '화이트', 500, 10, 0) RETURNING seq INTO v_opt2;
    INSERT INTO product_image (seq, product_seq, image_url, public_id, thumbnail_yn)
    VALUES (product_image_seq.NEXTVAL, v_prod, '/src/images/product/gprox.jpg', 'gprox', 'Y');
    INSERT INTO group_buy (seq, product_seq, start_at, end_at, created_at, min_count, max_count, original_price, final_price, status)
    VALUES (group_buy_seq.NEXTVAL, v_prod, v_now - INTERVAL '1' DAY, v_now + INTERVAL '7' DAY, v_now,
            1, 300, 200000, 140000, 'ONGOING') RETURNING seq INTO v_gb2;
    INSERT INTO group_buy_options (seq, group_buy_seq, options_seq, order_qty, occupied_count)
    VALUES (group_buy_options_seq.NEXTVAL, v_gb2, v_opt1, 1, 0) RETURNING seq INTO v_g2o1;   -- 블랙: 정원 1, 비움
    INSERT INTO group_buy_options (seq, group_buy_seq, options_seq, order_qty, occupied_count)
    VALUES (group_buy_options_seq.NEXTVAL, v_gb2, v_opt2, 299, 0) RETURNING seq INTO v_g2o2;  -- 화이트: 여유
    -- 화이트 참여 12명 (회원 26..37)
    FOR i IN 26..37 LOOP
        INSERT INTO participation (seq, group_buy_seq, group_buy_options_seq, member_seq, status, created_at)
        VALUES (participation_seq.NEXTVAL, v_gb2, v_g2o2, v_mem(i), 'PARTICIPATING', v_now);
    END LOOP;

    -- =========================================================
    -- 3) [시연] 젠하이저 IE900 이어폰 | 이어폰 | 100만→60만 | ONGOING
    --    매진 옵션(실버: 정원 10·참여 10 → 매진) + 대기열 4명. 블랙은 참여 18명(여유).
    -- =========================================================
    SELECT seq INTO v_cat FROM category WHERE category_name = '이어폰' AND depth_level = 2 AND ROWNUM = 1;
    INSERT INTO product (seq, seller_seq, category_seq, product_name, price, content, approval_status)
    VALUES (product_seq.NEXTVAL, v_seller, v_cat, '[시연] 젠하이저 IE900 이어폰', 1000000,
            '젠하이저 플래그십 유선 이어폰 IE 900. X3R 트랜스듀서.', 'APPROVED')
    RETURNING seq INTO v_prod;
    INSERT INTO options (seq, product_seq, color, stock, safety_stock, additional_price)
    VALUES (options_seq.NEXTVAL, v_prod, '실버', 500, 10, 0) RETURNING seq INTO v_opt1;
    INSERT INTO options (seq, product_seq, color, stock, safety_stock, additional_price)
    VALUES (options_seq.NEXTVAL, v_prod, '블랙', 500, 10, 0) RETURNING seq INTO v_opt2;
    INSERT INTO product_image (seq, product_seq, image_url, public_id, thumbnail_yn)
    VALUES (product_image_seq.NEXTVAL, v_prod, '/src/images/product/ie900.jpg', 'ie900', 'Y');
    INSERT INTO group_buy (seq, product_seq, start_at, end_at, created_at, min_count, max_count, original_price, final_price, status)
    VALUES (group_buy_seq.NEXTVAL, v_prod, v_now - INTERVAL '1' DAY, v_now + INTERVAL '5' DAY, v_now,
            20, 300, 1000000, 600000, 'ONGOING') RETURNING seq INTO v_gb3;
    INSERT INTO group_buy_options (seq, group_buy_seq, options_seq, order_qty, occupied_count)
    VALUES (group_buy_options_seq.NEXTVAL, v_gb3, v_opt1, 10, 0) RETURNING seq INTO v_g3o1;   -- 실버: 정원 10
    INSERT INTO group_buy_options (seq, group_buy_seq, options_seq, order_qty, occupied_count)
    VALUES (group_buy_options_seq.NEXTVAL, v_gb3, v_opt2, 290, 0) RETURNING seq INTO v_g3o2;  -- 블랙: 여유
    -- 실버 참여 10명 (38..47) → occupied 10 = 정원 10 → 매진
    FOR i IN 38..47 LOOP
        INSERT INTO participation (seq, group_buy_seq, group_buy_options_seq, member_seq, status, created_at)
        VALUES (participation_seq.NEXTVAL, v_gb3, v_g3o1, v_mem(i), 'PARTICIPATING', v_now);
    END LOOP;
    -- 블랙 참여 18명 (48..65)
    FOR i IN 48..65 LOOP
        INSERT INTO participation (seq, group_buy_seq, group_buy_options_seq, member_seq, status, created_at)
        VALUES (participation_seq.NEXTVAL, v_gb3, v_g3o2, v_mem(i), 'PARTICIPATING', v_now);
    END LOOP;
    -- 실버(매진) 대기열 4명 (66..69) — FIFO 순서 위해 created_at 분 단위로 차등
    FOR i IN 66..69 LOOP
        INSERT INTO waiting_queue (seq, group_buy_seq, group_buy_options_seq, member_seq, created_at)
        VALUES (waiting_queue_seq.NEXTVAL, v_gb3, v_g3o1, v_mem(i), v_now - NUMTODSINTERVAL(70 - i, 'MINUTE'));
    END LOOP;

    -- =========================================================
    -- 4) [시연] 에이수스 제피러스 노트북 | 노트북 | 200만→160만 | ONGOING
    --    마감 임박(남은 2시간). 참여자 없음(장시간 시연 중 자동 마감 시 환불 대상 없게).
    -- =========================================================
    SELECT seq INTO v_cat FROM category WHERE category_name = '노트북' AND depth_level = 2 AND ROWNUM = 1;
    INSERT INTO product (seq, seller_seq, category_seq, product_name, price, content, approval_status)
    VALUES (product_seq.NEXTVAL, v_seller, v_cat, '[시연] 에이수스 제피러스 노트북', 2000000,
            'ASUS ROG Zephyrus G16 게이밍 노트북. OLED 디스플레이.', 'APPROVED')
    RETURNING seq INTO v_prod;
    INSERT INTO options (seq, product_seq, color, stock, safety_stock, additional_price)
    VALUES (options_seq.NEXTVAL, v_prod, '그레이', 500, 10, 0) RETURNING seq INTO v_opt1;
    INSERT INTO product_image (seq, product_seq, image_url, public_id, thumbnail_yn)
    VALUES (product_image_seq.NEXTVAL, v_prod, '/src/images/product/asusnotebook.png', 'asusnotebook', 'Y');
    INSERT INTO group_buy (seq, product_seq, start_at, end_at, created_at, min_count, max_count, original_price, final_price, status)
    VALUES (group_buy_seq.NEXTVAL, v_prod, v_now - INTERVAL '3' DAY, v_now + INTERVAL '2' HOUR, v_now,
            30, 300, 2000000, 1600000, 'ONGOING') RETURNING seq INTO v_gb4;
    INSERT INTO group_buy_options (seq, group_buy_seq, options_seq, order_qty, occupied_count)
    VALUES (group_buy_options_seq.NEXTVAL, v_gb4, v_opt1, 300, 0) RETURNING seq INTO v_g4o1;

    -- =========================================================
    -- 5) [시연] 뉴발란스 992 신발 | 운동화 | 30만→26만 | SCHEDULED(시작 전)
    --    예정 공구 노출(시작까지 카운트다운). 사이즈 3종. 시작 전이라 참여 0.
    -- =========================================================
    SELECT seq INTO v_cat FROM category WHERE category_name = '운동화' AND depth_level = 2 AND ROWNUM = 1;
    INSERT INTO product (seq, seller_seq, category_seq, product_name, price, content, approval_status)
    VALUES (product_seq.NEXTVAL, v_seller, v_cat, '[시연] 뉴발란스 992 신발', 300000,
            '뉴발란스 992 메이드 인 USA. 그레이 클래식.', 'APPROVED')
    RETURNING seq INTO v_prod;
    INSERT INTO options (seq, product_seq, options_size, stock, safety_stock, additional_price)
    VALUES (options_seq.NEXTVAL, v_prod, '260', 500, 10, 0) RETURNING seq INTO v_opt1;
    INSERT INTO options (seq, product_seq, options_size, stock, safety_stock, additional_price)
    VALUES (options_seq.NEXTVAL, v_prod, '270', 500, 10, 0) RETURNING seq INTO v_opt2;
    INSERT INTO options (seq, product_seq, options_size, stock, safety_stock, additional_price)
    VALUES (options_seq.NEXTVAL, v_prod, '280', 500, 10, 0) RETURNING seq INTO v_opt3;
    INSERT INTO product_image (seq, product_seq, image_url, public_id, thumbnail_yn)
    VALUES (product_image_seq.NEXTVAL, v_prod, '/src/images/product/newbalance992.jpg', 'newbalance992', 'Y');
    INSERT INTO group_buy (seq, product_seq, start_at, end_at, created_at, min_count, max_count, original_price, final_price, status)
    VALUES (group_buy_seq.NEXTVAL, v_prod, v_now + INTERVAL '2' DAY, v_now + INTERVAL '9' DAY, v_now,
            50, 300, 300000, 260000, 'SCHEDULED') RETURNING seq INTO v_gb5;
    INSERT INTO group_buy_options (seq, group_buy_seq, options_seq, order_qty, occupied_count)
    VALUES (group_buy_options_seq.NEXTVAL, v_gb5, v_opt1, 100, 0) RETURNING seq INTO v_g5o1;
    INSERT INTO group_buy_options (seq, group_buy_seq, options_seq, order_qty, occupied_count)
    VALUES (group_buy_options_seq.NEXTVAL, v_gb5, v_opt2, 100, 0) RETURNING seq INTO v_g5o2;
    INSERT INTO group_buy_options (seq, group_buy_seq, options_seq, order_qty, occupied_count)
    VALUES (group_buy_options_seq.NEXTVAL, v_gb5, v_opt3, 100, 0) RETURNING seq INTO v_g5o3;

    -- =========================================================
    -- occupied_count 동기화: 활성 참여 수(PARTICIPATING + PAYMENT_PENDING)와 일치 (NFR-003)
    -- =========================================================
    UPDATE group_buy_options gbo
    SET occupied_count = (
        SELECT COUNT(*) FROM participation p
        WHERE p.group_buy_options_seq = gbo.seq
          AND p.status IN ('PARTICIPATING','PAYMENT_PENDING'))
    WHERE gbo.seq IN (v_g1o1, v_g2o1, v_g2o2, v_g3o1, v_g3o2, v_g4o1, v_g5o1, v_g5o2, v_g5o3);

    COMMIT;
    DBMS_OUTPUT.PUT_LINE('시연 공구 5건 + 회원 70명 + 참여/대기열 등록 완료.');
    DBMS_OUTPUT.PUT_LINE('진행중 gb seq: ' || v_gb1 || ',' || v_gb2 || ',' || v_gb3 || ',' || v_gb4
                         || ' / 예정 gb seq: ' || v_gb5);

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('시연 더미 생성 중 오류: ' || SQLERRM);
        RAISE;
END;
/

-- 관련 시퀀스를 각 테이블 max(seq)+1로 보정 (이후 화면/앱 INSERT 충돌 방지)
DECLARE
    v_max NUMBER;
    PROCEDURE fix(p_tab VARCHAR2, p_seq VARCHAR2) IS
    BEGIN
        EXECUTE IMMEDIATE 'SELECT NVL(MAX(seq),0)+1 FROM ' || p_tab INTO v_max;
        EXECUTE IMMEDIATE 'ALTER SEQUENCE ' || p_seq || ' RESTART START WITH ' || v_max;
    END;
BEGIN
    fix('product',            'product_seq');
    fix('options',            'options_seq');
    fix('product_image',      'product_image_seq');
    fix('group_buy',          'group_buy_seq');
    fix('group_buy_options',  'group_buy_options_seq');
    fix('member',             'member_seq');
    fix('participation',      'participation_seq');
    fix('waiting_queue',      'waiting_queue_seq');
    DBMS_OUTPUT.PUT_LINE('시퀀스 8개 보정 완료.');
END;
/

-- 확인용: 생성된 시연 공구의 현재 참여 인원/대기열 (선택 실행)
SELECT gb.seq AS gb_seq, p.product_name, gb.status, gb.min_count, gb.max_count,
       (SELECT COUNT(*) FROM participation pa
         WHERE pa.group_buy_seq = gb.seq AND pa.status = 'PARTICIPATING') AS participating,
       (SELECT COUNT(*) FROM waiting_queue wq
         WHERE wq.group_buy_seq = gb.seq) AS waiting
FROM group_buy gb JOIN product p ON p.seq = gb.product_seq
WHERE p.product_name LIKE '[시연]%'
ORDER BY gb.seq;
