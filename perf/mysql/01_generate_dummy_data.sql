-- MySQL 8.x
-- 목적: product 100,000건 + orders 100,000건 더미 생성
-- 실행 전: USE market_db;

SET SESSION cte_max_recursion_depth = 1000000;

-- 필요 시 초기화
-- DELETE FROM orders;
-- DELETE FROM product;

-- 1) product 100,000건 생성
INSERT INTO product (product_token, seller_id, name, price, status, created_at, updated_at)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 100000
)
SELECT
    CONCAT('prd_', LPAD(n, 10, '0')) AS product_token,
    (n % 1000) + 1 AS seller_id,                                 -- 판매자 1,000명 분산
    CONCAT('product_', n) AS name,
    1000 + (n % 90000) AS price,
    CASE
        -- 저카디널리티 실험용: 약 90%를 완료 상태(END_OF_SALE)로 생성
        WHEN (n % 10) < 9 THEN 'END_OF_SALE'
        -- 나머지 10%를 다른 상태로 분산
        WHEN (n % 3) = 0 THEN 'PREPARE'
        WHEN (n % 3) = 1 THEN 'ON_SALE'
        ELSE 'RESERVED'
    END AS status,
    NOW(),
    NOW()
FROM seq;

-- 2) orders 100,000건 생성
INSERT INTO orders (buyer_id, product_id, price, status, created_at, updated_at)
WITH RECURSIVE seq2 AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq2 WHERE n < 100000
)
SELECT
    (n % 5000) + 1 AS buyer_id,                                   -- 구매자 5,000명 분산
    (n % 100000) + 1 AS product_id,                               -- product FK 범위
    1000 + (n % 90000) AS price,
    CASE (n % 5)
        WHEN 0 THEN 'INIT'
        WHEN 1 THEN 'ORDER_CANCEL'
        WHEN 2 THEN 'ORDER_SALE_APPROVED'
        WHEN 3 THEN 'ORDER_RESERVE'
        ELSE 'ORDER_COMPLETE'
    END AS status,
    NOW(),
    NOW()
FROM seq2;

-- 검증
SELECT COUNT(*) AS product_cnt FROM product;
SELECT COUNT(*) AS orders_cnt FROM orders;
