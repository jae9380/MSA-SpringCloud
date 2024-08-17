# SpringCloud 실습 프로젝트

## Catalog Service

### 환경

- java version : 17
- spring boot version : 3.2.4
- build tool : Maven
- dependency :
  - Spring Cloud Discovery - Netflix Eureka Client
  - Developer Tools - Lombok
  - Web - Spring Web
  - Data - Spring Data JPA
  - Database - H2 Database
  - Object Mapping - ModelMapper

### 내용

간단한 상품 목록만 나타내는 프로젝트

- APIs

| 기능                     | 마이크로 서비스       | URI                             | HTTP Method | 여부 |
| ------------------------ | --------------------- | ------------------------------- | ----------- | ---- |
| 상품 목록 조회           | Catalogs Microservice | /catalog-service/catalogs       | GET         | O    |
| 사용자 별 상품 주문      | Orders Microservice   | /order-service/{user_id}/orders | POST        | X    |
| 사용자 별 주문 내역 조회 | Orders Microservice   | /order-service/{user_id}/orders | GET         | X    |
