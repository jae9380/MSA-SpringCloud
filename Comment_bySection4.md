# Section 4

## Catalogs and Order Microservice

<details>
  <summary>part 4 / </summary>
  <div markdown="1">

Catalogs-Service 프로젝트 생성 후 yml파일의 내용을 User-Service의 yml 내용 붙여넣기

```yaml
jpa:
  hibernate:
    ddl-auto: create-drop
  show-sql: true
  generate-ddl: true
```

추가적으로 JPA 설정 내용 작성

- `ddl-auto: create-drop`
  Hibernate가 어플리케이션 엔티티 클래스에 따라 데이터베이스 스키마를 어떻게 설정하는 내용이다.  
  여기서 `create-drop`로 설정 했을 때 어플리케이션이 시작할 때 데이터베이스 스키마를 생성하고, 종료를 하였을 때는 해당 스키마를 삭제하는 설정이다.
- `show-spl: true`
  Hibernate가 실행하는 SQL 쿼리를 콘솔에 출력하도록 설정
- `generate-ddl: true`
  JPA가 어플리케이션의 엔티티 클래스에 따라 데이터베이스 스키마를 생성 여부를 설정한다.

`data.sql` 파일을 생성하여 초기데이터 생성

```sql
insert into catalog(product_id, product_name, stock, unit_price)
    values('CATALOG-001', 'Berlin', 100, 1500);
insert into catalog(product_id, product_name, stock, unit_price)
    values('CATALOG-002', 'Tokyo', 110, 1000);
insert into catalog(product_id, product_name, stock, unit_price)
    values('CATALOG-003', 'Stockholm', 120, 2000);
```

이후 API-Gateway 등록하기 위해서 api-gateway 프로젝트에서 설정 작업 진행

작업이 잘 진행되었는지 확인하기 위해서 Postman에서 `127.0.0.1:8000/catalog-service/catalogs` GET요청을 날려서 앞에서 데이터베이스에 입력한 데이터가 출력되면 작업이 잘 끝났다.

  </div>
</details>

_토글_

```html
<details>
  <summary>part</summary>
  <div markdown="1"></div>
</details>
```
