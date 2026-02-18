# Market API

사용자간 거래가 가능한 Market API를 생성합니다. 요구사항에 맞춰 진행합니다

Test 코드 작성을 통해 가독성과 코드 품질을 향상시킵니다.

## 목차

- [Architecture Layers](#architecture-layers)
- [Exception 핸들링](#exception-handling)
- [요구사항](#requirements)
- [API 명세](#api-spec)
- [DB 성능 개선](#db-performance)

<a id="architecture-layers"></a>
# Architecture Layers

Layer 간의 참조 관계에서는 단방향 의존 유지를 통해 확장에 유연한 아키텍처로 설계합니다.

간결하고 읽기 쉬운 코드로 작성합니다.

책임과 역할을 잘 구분합니다.

<br>

**Layer 간 참조 관계**
- **application**과 **infrastructure** 는 **domain Layer** 를 바라보게 하고 양방향 참조는 허용하지 않습니다.

<br>

**Application Layer**
- **transaction으로 묶여야 하는** 도메인 로직과 그렇지 않는 로직을 구분합니다.
- 예를 들어 **회원가입** 후 **회원가입 성공 이메일 발송**의 경우 이메일 발송인 외부 서비스 call은 회원가입 도메인 로직에 포함되지 
  않으므로 **transaction에서 분리**시키며 **회원가입 도메인 로직**은 **외부 서비스 call 성공 / 실패 여부**에 대해 크게 **민감하지 않게 처리**된다.

<br>

**Domain Layer**
- **domain Layer**는 **low level**의 기술에 관계없이 **독립적으로 존재**합니다.
- **domain layer** 에서는 도메인 **로직의 흐름을 표현**하고 구현하는 Service
  와 ServiceImpl 이 있지만 그 외의 상세한 구현은 Reader, Store, Executor
  같은 **interface 를 선언**하여 사용하고 이에 대한 **실제 구현체는
  Infrastructure layer** 에 두고 활용합니다.

<br>

**Infrastructure Layer**
- domain layer 에 선언되고 사용되는 추상화된 interface 를 실제로 구현하여 runtime
  시에는 실제 로직이 동작합니다.
- Service 간의 참조 관계는 막았지만, Infrastructure layer 에서의 구현체 간에는 참조
  관계를 허용합니다.

<br>

**Interfaces Layer**
- API 를 설계할 때에는 없어도 되는 Request Parameter 는 제거하고, 외부에 리턴하
  는 Response 도 최소한을 유지하도록 노력합니다.
- http, gRPC, 비동기 메시징과 같은 서비스간 통신 기술은 Interfaces layer 에서만 사
  용되도록 합니다.

<br>


**Interfaces**

- xxApiController
- xxDto

**Application Layer**

- xxFacade

**Domain Layer**

- Entity
- xxService
- xxServiceImpl
- xxReader
- xxStore
- xxCommand
- xxInfo
- xxManager
- xxManagerImpl
- xxMapper

**Infrastructure**

- xxReaderImpl
- xxRepository
- xxStoreImpl

<br>

<a id="exception-handling"></a>
# Exception 핸들링

가독성과 편의를 위해 표준예외를 사용합니다.

- 잘못된 인자 입력시 IllegalArgumentException 을 반환합니다.
- 잘못된 상태일 경우 IllegalStateException 를 반환합니다.

시스템 예외 상황 (집중 모니터링 처리) 와 비즈니스 로직 에러 상황을 `GlobalControllerAdvice` 에서 구분하여 처리합니다.

- 시스템 예외 / http status : 500 AND result : FAIL
- 비즈니스 로직 에러 / http status : 200 AND result : FAIL
- 잘못된 인자 입력 예외 / http status : 400 AND result : FAIL


<br>

<a id="requirements"></a>
# 요구사항

1단계
- 유저는 제품등록을 할 수 있습니다.
- 등록된 제품에는 "제품명", "가격", "예약상태"가 포함되어야하고, 목록조회와 상세조회시에 예약상태를 포함해야합니다.
- 제품의 상태는 "판매중", "예약중", "완료" 세가지가 존재합니다.
- 구매자가 제품의 상세페이지에서 구매하기 버튼을 누르면 거래가 시작됩니다.
- 판매자와 구매자는 제품의 상세정보를 조회하면 당사자간의 거래내역을 확인할 수 있습니다.
- 모든 사용자는 내가 "구매한 용품(내가 구매자)"과 "예약중인 용품(내가 구매자/판매자 모두)"의 목록을 확인할 수 있습니다.
- 판매자는 거래진행중인 구매자에 대해 '판매승인'을 하는 경우 거래가 완료됩니다.

2단계
- 제품에 수량이 추가됩니다. 제품정보에 "제품명", "가격", "예약상태", "수량"이 포함되어야합니다.
- 다수의 구매자가 한 제품에 대해 구매하기가 가능합니다. (단, 한 명이 구매할 수 있는 수량은 1개뿐입니다.)
- 구매확정의 단계가 추가됩니다. 구매자는 판매자가 판매승인한 제품에 대해 구매확정을 할 수 있습니다.
- 거래가 시작되는 경우 수량에 따라 제품의 상태가 변경됩니다.
- 추가 판매가 가능한 수량이 남아있는 경우 - 판매중
- 추가 판매가 불가능하고 현재 구매확정을 대기하고 있는 경우 - 예약중
- 모든 수량에 대해 모든 구매자가 모두 구매확정한 경우 - 완료
- "구매한 용품"과 "예약중인 용품" 목록의 정보에서 구매하기 당시의 가격 정보가 나타나야합니다. 
    - 예) 구매자 A가 구매하기 요청한 당시의 제품 B의 가격이 3000원이었고 이후에 4000원으로 바뀌었다 하더라도 목록에서는 3000원으로 나타나야합니다.
    - 여기서의 예약중은 상품의 상태 예약중이 아닌 거래 상태의 예약중을 의미한다.

<br>

# 공통

구매취소는 고려하지 않습니다.
검증이 필요한 부분에 대해 테스트코드를 작성해주세요.
작성한 API에 대한 명세를 작성해주세요.

<br>

<a id="api-spec"></a>
# API 명세

## 제품

### 제품 등록

#### [POST] /api/v1/products

> name: 제품명
>
> price: 제품 가격

### 구매한 제품 조회

#### GET /api/v1/products?buyerId&status=PURCHASED

> buyerId: 구매자 Id

### 예약한 제품 판매자가 조회

#### GET /api/v1/products?buyerId&status=RESERVED

> sellerId: 판매자 Id

### 예약한 제품 구매자가 조회

#### GET /api/v1/products?sellerId&status=RESERVED

> buyerId: 구매자 Id

<br>

## 주문

### 주문 등록

#### [POST] /api/v1/orders

> buyerId: 구매자 Id
> productId: 상품 Id
> price: 가격

### 제품 구매 승인

#### [POST] /api/v1/orders/approve

> productId: 제품 Id
> sellerId : 판매자 Id
> orderId : 거래 Id

### 제품 구매 확정

#### [POST] /api/v1/orders/complete

> productId: 제품 Id
> sellerId : 판매자 Id
> orderId : 거래 Id

### 주문 조회

#### [GET] /api/v1/orders?buyerId=123

> buyerId: 구매자 Id

<br>

## 유저

### 유저 등록

#### [POST] /api/v1/users

> email : 이메일
> username: 이름
> password: 비밀번호

<br>

<a id="db-performance"></a>
# DB 성능 개선

## 실험 방법 및 재현 가이드

정확한 성능 비교를 위해, 동일 데이터셋과 동일 쿼리 조건에서 인덱스 생성/삭제 전후를 반복 측정했습니다.

- 더미 데이터 생성 SQL: `perf/mysql/01_generate_dummy_data.sql`
  - `product` 100,000건 이상, `orders` 100,000건 이상 데이터 생성
- 인덱스 관리 SQL: `perf/mysql/02_index_manage.sql`
  - 실험 대상 인덱스 생성/삭제 스크립트 제공
- 벤치마크 쿼리 SQL: `perf/mysql/03_benchmark_queries.sql`
  - `EXPLAIN ANALYZE` 기반 조회 성능 비교 쿼리 제공
- 성능 측정 테스트 코드: `src/test/java/sample/market/performance/IndexPerformanceReportTest.java`
  - 인덱스 전/후 시간을 측정하고 요약표 형태로 출력
  - 수동 실행 전용: `-DrunPerfTest=true` 옵션 필요

실행 순서:
1. docker compose 로 MySQL 컨테이너 실행 후 더미 데이터 적재
2. 인덱스 없는 상태/있는 상태를 기준으로 `EXPLAIN ANALYZE` 비교
3. 성능 테스트 코드 실행으로 수치 요약
4. 결과를 표와 상세 분석으로 문서화

테스트 실행 예시:
```bash
./gradlew test --tests "sample.market.performance.IndexPerformanceReportTest" -DrunPerfTest=true
```

## 성능 개선 요약

| 기능 | 인덱스 전 쿼리 시간 | 인덱스 후 쿼리 시간 | 개선율 | 비고 |
|---|---:|---:|---:|---|
| 주문 조회 (`orders`), 인덱스 없음 -> 단일 인덱스 | 47.2ms | 1.13ms | 약 97.6% 개선 | 단일 인덱스 (`buyer_id`), `product_id`는 후속 필터 |
| 주문 조회 (`orders`), 인덱스 없음 -> 복합 인덱스 | 47.2ms | 0.529ms | 약 98.9% 개선 | 복합 인덱스 (`buyer_id`, `product_id`), 조건 매칭 최적 |
| 주문 조회 (`orders`), 단일 인덱스 -> 복합 인덱스 | 1.13ms | 0.529ms | 약 53.2% 개선 | 단일 인덱스 대비 추가 개선 |
| 상품 조회 (`product.status`) 인덱스 적용 | 20.1ms | 20.3ms | 약 1.0% 성능 저하 | 단일 인덱스 (status, 카디널리티가 낮음, 유의미 개선 없음) |
| 상품 조회 + OFFSET 페이징 | - | 10.2ms | - | `OFFSET 20000` |
| 상품 조회 + 커서 페이징 | - | 0.389ms | - | `id < 20000` offset 대비 약 96.2% 개선 |

## 상세 분석

### 1) 주문 조회 인덱스 비교 (`buyer_id`, `product_id`)

조회 패턴:
- `WHERE buyer_id = ? AND product_id = ? ORDER BY id DESC LIMIT 100`

#### 1-1. 인덱스 전(실질적으로 PK 역순 전체 스캔)

```sql
EXPLAIN ANALYZE
SELECT *
FROM orders
WHERE buyer_id = 123
  AND product_id = 123
ORDER BY id DESC
LIMIT 100;
```

```text
actual time=47.2..47.2
Index scan on orders using PRIMARY (reverse) ... rows=100000
```

- `buyer_id`, `product_id`를 바로 타지 못해 full table scan 후 필터링이 발생했습니다.

#### 1-2. 단일 인덱스 (`buyer_id_idx`) 사용
```sql
EXPLAIN ANALYZE
SELECT *
FROM orders
WHERE buyer_id = 123
  AND product_id = 123
ORDER BY id DESC
LIMIT 100;
```

```text
actual time=1.05..1.13
Index lookup on orders using buyer_id_idx (buyer_id=123)
```

- `buyer_id` 조건으로 후보군을 빠르게 줄여 성능이 크게 개선되었습니다.
- 다만 `product_id`는 추가 비용이 남습니다.

#### 1-3. 복합 인덱스 (`buyer_product_idx`) 사용
```sql
EXPLAIN ANALYZE
SELECT *
FROM orders
WHERE buyer_id = 123
  AND product_id = 123
ORDER BY id DESC
LIMIT 100;
```

```text
actual time=0.444..0.529
Index lookup on orders using buyer_product_idx (buyer_id=123, product_id=123)
```

- 조회 조건이 복합 인덱스 선두 컬럼 순서와 일치하여 가장 효율적인 경로를 탔습니다.

### 2) 페이징 성능 비교

#### 2-0. 상태 필터 인덱스 비효율 (저카디널리티)

```sql
EXPLAIN ANALYZE
SELECT p.*
FROM product p
WHERE p.status = 'END_OF_SALE'
ORDER BY p.id DESC
LIMIT 20 OFFSET 20000;
```

```text
인덱스 전: actual time=20.1..20.1
인덱스 후: actual time=20.3..20.3
```

- `status = 'END_OF_SALE'` 조건이 대량 매칭되는 분포에서는 인덱스 선택도가 낮습니다.
- 이번 실측에서는 인덱스 적용 전/후 차이가 거의 없었고(약 1% 저하), 성능 개선 효과가 확인되지 않았습니다.
- 이 구간의 병목은 상태 인덱스보다 `OFFSET` 스캔 비용 영향이 더 큽니다.

#### 2-1. OFFSET 페이징

```sql
EXPLAIN ANALYZE
SELECT p.*
FROM product p
ORDER BY p.id DESC
LIMIT 20 OFFSET 20000;
```

```text
actual time=10.2..10.2 rows=20 loops=1
```

- 큰 OFFSET을 건너뛰기 위해 불필요한 행을 많이 읽어야 하므로 비용이 큽니다.

#### 2-2. 커서 페이징
```sql
EXPLAIN ANALYZE
SELECT p.*
FROM product p
WHERE p.id < 20000
ORDER BY p.id DESC
LIMIT 20;
```

```text
actual time=0.379..0.389 rows=20 loops=1
Index range scan on p using PRIMARY over (id < 20000)
```

- PK 범위를 직접 좁혀 읽기 때문에 deep offset 비용을 피합니다.
- 동일 조건 기준 OFFSET(10.2ms) 대비 약 96.2% 개선되었습니다.


### 3) 결론

- 주문 조회는 `orders(buyer_id, product_id)` 복합 인덱스가 가장 효과적입니다.
- 단일 인덱스(`buyer_id`)도 의미 있는 개선은 있지만, 복합 인덱스가 추가 이점을 제공합니다.
- 페이징은 deep offset 구간에서 커서 기반이 훨씬 유리합니다.

## 결론 요약

- 인덱스 적용 결과, 주문 조회 핵심 구간에서 최대 약 98.9% 성능 개선을 확인했습니다.  
  - 특히 `orders(buyer_id, product_id)` 복합 인덱스는 단일 인덱스 대비도 추가 개선 효과가 있었습니다.  
  - 반면 `product.status`는 이번 실측에서 인덱스 전/후가 거의 동일해, 인덱스 효과가 크지 않았습니다.  
- 페이징은 deep offset 구간에서 커서 기반(`id < cursor`)이 일반 OFFSET 방식보다 훨씬 안정적인 성능을 보였습니다.

## CQRS 관점 인프라 요약

- 조회(Read)는 리플리카 DB(슬레이브)로 분리하고, 쓰기(Write)는 마스터 DB에서 처리하는 구조가 유효합니다.
- 이 구조는 읽기 부하 분산, 트래픽 피크 대응, 수평 확장성 확보에 유리합니다.
