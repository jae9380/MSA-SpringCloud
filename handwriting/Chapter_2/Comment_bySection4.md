# Section 4

데이터 동기화를 위해서 `Order Service`에 요청된 정보를 `Catalogs Service`에 반영을 할 것이다.  
 `Order Service`에서 `Kafka Topic`으로 메세지 전송, `Catalogs Service`에서 `Kafka Topic`에 전송된 메세지 취득

![](https://i.postimg.cc/3RsFXb3Z/2024-09-15-104634.png)

 <details>
  <summary>part 1 / Database Synchronization Test Using Kafka </summary>
  <div markdown="1"></div>
</details>

<details>
  <summary>part 2 / Using a single database with Kafka Connect</summary>
  <div markdown="1">
  
Order-Service 2개를 기동할 때 해당 서비스는 각각의 데이터베이스를 갖고있기 때문에 `Kafka Connect`를 이용하여 단일 데이터베이스 사용을 할 수 있도록 해결

일단 Order-Service를 2개 기동 후 주문을 5번 시도 했을 때 각각 3개와 2개의 데이터가 각 서비스 데이터 베이스에 저장되는 것을 파악

이는 데이터를 확인 할 때 분산저장이 되었기 때문에 동기화 부분에서 문제가 발생한다.

각 Order-Service에 요청된 정보를 데이터베이스가 아닌 `Kafka Topic`으로 전송
`Kafka Topic`에 설정 된 `Kafka Sink Connect`를 이용하여 단일 데이터베이스에 저장을 통해 동기화를 할 예정이다.

기존 Order-Service H2에서 MariaDB로 변경

```json
{
  "schema": {
    "type": "struct",
    "fields": [
      { "type": "String", "optional": true, "field": "order_id" },
      { "type": "string", "optional": true, "field": "user_id" },
      { "type": "string", "optional": true, "field": "product_id" },
      { "type": "int32", "optional": true, "field": "pty" },
      { "type": "int32", "optional": true, "field": "total_price" },
      { "type": "int32", "optional": true, "field": "unit-price" }
    ],
    "optional": false,
    "name": "orders"
  },
  "payload": {
    "order_id": "~~~",
    "user_id": "user1",
    "product_id": "CATALOG-001",
    "qty": 5,
    "total_price": 6000,
    "unit_price": 1200
  }
}
```

이러한 형식으로 작성을 해야 데이터베이스에 저장이 되기 때문에 해당 포멧을 사용하기 위해 추가적인 클래스 생성

이후 아래와 같이 추가적인 connector 등록

```json
{
  "name": "my-order-sink-connect",
  "config": {
    "connector.class": "io.confluent.connect.jdbc.JdbcSinkConnector",
    "connection.url": "jdbc:mariadb://localhost:3306/mydb",
    "connection.user": "root",
    "connection.password": "test123",
    "auto.create": "true",
    "auto.evolve": "true",
    "delete.enabled": "false",
    "tasks.max": "1",
    "topics": "orders"
  }
}
```

> ## `org.apache.kafka.connect.errors.DataException: Unknown schema type: String` 문제 발생
>
> `my-order-sink-connect` 등록 후 상세 정보를 확인을 했는데 해당 커넥트가 `"state": "RUNNING"` 으로 나타나지 않고 `org.apache.kafka.connect.errors.DataException: Unknown schema type: String` 와 같은 문제가 발생되었다.  
> 이는 `Schema`의 `Field` 데이터 타입을 `String`으로 작성을 했기 때문에 발생하는 문제이다.  
> 그래서
>
> ```java
>    List<Field> fields = Arrays.asList(
>            new Field ("string",true,"order_id"),
>            new Field ("string",true,"user_id"),
>            new Field ("string",true,"product_id"),
>            new Field ("int32",true,"qty"),
>            new Field ("int32",true,"unit_price"),
>            new Field ("int32",true,"total_price")
>    );
> ```
>
> `String`을 `string`으로 변경을 한다.  
> 마지막으로 해당 토픽을 삭제를 해주면 된다.
> 여기서 Mac OS나 리눅스 경우에는 해당 토픽을 삭제하면 가능하지만, Windows 환경에서는 토픽을 삭제하는 경우 에러가 발생한다. 그래서 D드라이브 내부 `Kafka`로그와 `Zookeeper`로드 전체를 삭제해야 한다.

이제 등록이 잘 되었다면 확인을 위해서 2개가 기동된 Order-Service로 주문을 날려서 확인을 하자

주문 정보를 날릴 때 각각의 서비스로 분할하여 주문이 접수가 되었다. 그리고 데이터베이스를 확인하면 모든 주문 데이터들이 잘 들어와있다.

  </div>
</details>

_토글_

```html
<details>
  <summary>part</summary>
  <div markdown="1"></div>
</details>
```
