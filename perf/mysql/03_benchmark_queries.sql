-- MySQL 8.x
-- 목적: README "DB 성능 개선" 섹션과 동일한 벤치마크 쿼리
-- 실행 전: USE market_db;
-- 권장 순서:
-- 1) perf/mysql/02_index_manage.sql 로 인덱스 상태 제어
-- 2) 아래 EXPLAIN ANALYZE 쿼리 실행

SELECT 1;

-- =========================================================
-- 1) 주문 조회 인덱스 비교
-- =========================================================
-- 공통 쿼리:
-- WHERE buyer_id = 123 AND product_id = 123 ORDER BY id DESC LIMIT 100
-- (A) 인덱스 없음
-- (B) 단일 인덱스: orders(buyer_id)
-- (C) 복합 인덱스: orders(buyer_id, product_id)

EXPLAIN ANALYZE
SELECT *
FROM orders
WHERE buyer_id = 123
  AND product_id = 123
ORDER BY id DESC
LIMIT 100;

-- =========================================================
-- 2) status 저카디널리티 인덱스 비효율 비교
-- =========================================================
-- README의 "COMPLETE" 의미를 현재 스키마 값(END_OF_SALE)으로 측정
-- (A) 인덱스 없음: DROP INDEX idx_product_status ON product;
-- (B) 인덱스 있음: CREATE INDEX idx_product_status ON product(status);

EXPLAIN ANALYZE
SELECT p.*
FROM product p
WHERE p.status = 'END_OF_SALE';

-- 비율 확인(END_OF_SALE 비중)
SELECT
  COUNT(*) AS total_count,
  SUM(CASE WHEN status = 'END_OF_SALE' THEN 1 ELSE 0 END) AS end_of_sale_count,
  ROUND(SUM(CASE WHEN status = 'END_OF_SALE' THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS end_of_sale_pct
FROM product;

-- =========================================================
-- 3) 페이징 2종 비교
-- =========================================================
-- 3-1) 상태 필터 + OFFSET 페이징
EXPLAIN ANALYZE
SELECT p.*
FROM product p
ORDER BY p.id DESC
LIMIT 20 OFFSET 20000;

-- 3-2) 상태 필터 + 커서 페이징
EXPLAIN ANALYZE
SELECT p.*
FROM product p
WHERE p.id < 20000
ORDER BY p.id DESC
LIMIT 20;

