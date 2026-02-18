-- MySQL 8.x
-- 목적: 인덱스 생성/삭제 스크립트
-- 실행 전: USE market_db;

-- =========================================================
-- A) 구매한 상품 조회 관련
-- =========================================================
-- 정정: product.seller_id 기준 조회 성능 실험

CREATE INDEX idx_product_seller_id ON product (seller_id);
DROP INDEX idx_product_seller_id ON product;

-- =========================================================
-- B) 상품 조회 status(저카디널리티) 인덱스 실험
-- =========================================================
CREATE INDEX idx_product_status ON product (status);
DROP INDEX idx_product_status ON product;

-- status + id 조합(커서 페이징에서 비교용)
CREATE INDEX idx_product_status_id ON product (status, id);
DROP INDEX idx_product_status_id ON product;

-- =========================================================
-- C) 주문 제품 조회 복합 인덱스
-- =========================================================
CREATE INDEX idx_orders_buyer_product ON orders (buyer_id, product_id);
DROP INDEX idx_orders_buyer_product ON orders;

-- 비교용: status까지 포함
CREATE INDEX idx_orders_buyer_product_status ON orders (buyer_id, product_id, status);
DROP INDEX idx_orders_buyer_product_status ON orders;
