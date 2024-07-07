# Market API

사용자간 거래가 가능한 Wanted Market API를 생성해야합니다. 요구사항에 맞춰 진행해주세요. 요구사항은 공통과 1단계(필수), 2단계(선택) 로 나누어져 있습니다.

공통과 1단계는 필수로 진행해주시고, 2단계는 1단계를 마무리한 이후에 순차적으로 진행하시는 것을 추천합니다. 스프린트를 진행하면서 기능이 어떻게 발전해나가는지 사전 과제를 통해서 경험해봅니다.


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

# Exception 핸들링

가독성과 편의를 위해 표준예외를 사용합니다.

- 잘못된 인자 입력시 IllegalArgumentException 을 반환합니다.
- 잘못된 상태일 경우 IllegalStateException 를 반환합니다.

시스템 예외 상황 (집중 모니터링 처리) 와 비즈니스 로직 에러 상황을 `GlobalControllerAdvice` 에서 구분하여 처리합니다.

- 시스템 예외 / http status : 500 AND result : FAIL
- 비즈니스 로직 에러 / http status : 200 AND result : FAIL
- 잘못된 인자 입력 예외 / http status : 400 AND result : FAIL


<br>

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

<br>

# 공통

구매취소는 고려하지 않습니다.
검증이 필요한 부분에 대해 테스트코드를 작성해주세요.
작성한 API에 대한 명세를 작성해주세요.

<br>

# API 명세

## 제품

### 제품 등록

#### [POST] /api/v1/products

> name: 제품명
>
> price: 제품 가격

### 구매한 제품 조회

#### GET /products/purchase

> buyerId: 구매자 Id

### 예약한 제품 판매자가 조회

#### GET /products/reserved/seller

> sellerId: 판매자 Id

### 예약한 제품 구매자가 조회

#### GET /products/reserved/buyer

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
> sellerId : 구매자 Id
> orderId : 거래 Id

### 제품 구매 확정

#### [POST] /api/v1/orders/complete

> productId: 제품 Id
> sellerId : 구매자 Id
> orderId : 거래 Id

<br>

## 유저

### 유저 등록

#### [POST] /api/v1/users

> email : 이메일
>
> username: 이름
> 
> password: 비밀번호