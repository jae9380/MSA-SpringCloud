# Section 2

## Communication Types

<details>
  <summary>part 1 / Using Rest Template</summary>
  <div markdown="1">

```java
//        List<ResponseOrder> orders = new ArrayList<>();

//        Using as Rest Template
        String orderUrl = "http://127.0.0.1:8000/order-service/%s/orders ";

        ResponseEntity<List<ResponseOrder>> orderListResponse =
                restTemplate.exchange(orderUrl, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<ResponseOrder>>() {
        });
```

이렇게 주소값을 직접적으로 하드코딩으로 작업을 해도 큰 문제는 없겠지만, 나중에 변경되거나 게이트웨이의 IP나 포트번호가 변경되거나 같은 서버에 구동이 되지 않을 상황을 대비하여 별도로 구성파일에 만들어 두는 것이 좋다.

```yaml
order_service:
  url: http://127.0.0.1:8000/order-service/%s/orders
```

이 처럼 추가로 작성을 해주면 된다. 여기서 주의할 점은 자바파일에서 해당 url을 사용을 할 떄 `%s`부분이 있기 때문에 주의를 해야한다.

```java
String orderUrl = String.format(env.getProperty("order_service.url"),userId);
```

User Microservice에서 Order Service를 호울할 때 좀 더 간단한 방법을 사용하겠다.

기존에는 `Ip:Port Number`을 직접 작성을 했는데 이번에는

```yaml
order_service:
  url: http://order-service/order-service/%s/orders
```

유레카에 등록되어진 서비스의 이름을 입력하여 사용을 하겠다.

이를 설정하기 위해서 `@LoadBalanced` 어노테이션을 설정해줘야 한다.

  </div>
</details>

<details>
  <summary>part 2 / Feign Web Service Client</summary>
  <div markdown="1">
  
`pom.xml`에 `FeignClient`추가 후, 더 이상 `Rest Template`는 사용하지 않기 때문에 로드발랜서는 주석 처리 후 `@EnableFeignClients`어노테이션 추가

이후 `OrderServiceClient`인터페이스를 생성, 해당 인터페이스는 `@FeignClient(name = "order-service")` 어노테이션에 호출 할 서비스의 이름을 설정해준다.  
그리고 내부에 선언하고자 하는 메소드 전부 다 `Public`이다. 그래서 따로 지정을 해주지 않아도 된다.

```java

@FeignClient(name = "order-service")
public interface OrderServiceClient {

    @GetMapping("/order-service/{userId}/orders")
    List<ResponseOrder>  getOrders(@PathVariable String userId);
}
```

인터페이스 생성 후, 해당 코드를 이용할 서비스로직으로 이동하여 생성자를 주입하고

기존에 이용하던 `Rest Template`는 주석으로 처리 후 `Feign Client` 방법으로 변경

```java
////        Using as Rest Template
//        String orderUrl = String.format(env.getProperty("order_service.url"),userId);
//
//        ResponseEntity<List<ResponseOrder>> orderListResponse =
//                restTemplate.exchange(orderUrl, HttpMethod.GET, null,
//                new ParameterizedTypeReference<List<ResponseOrder>>() {
//        });
////        restTemplate.exchange( 매개 값 -> 주소, 메소드 타입, 요청할 때 파라미터, 전달 받고자하는 방식)
//        List<ResponseOrder> ordersList = orderListResponse.getBody();

//        Using a feign Client
        List<ResponseOrder> ordersList = orderServiceClient.getOrders(userId);

        userDto.setOrders(ordersList);
```

변경을 하면 기존에는 여러 줄의 코드를 작성을 해야 했다면 해당 방법은 인터페이스 선언 후 한 줄의 코드로 작성할 수 있게 된다.

  </div>
</details>

<details>
  <summary>part 3 / Tracing logs in Feign Client</summary>
  <div markdown="1">
  
```yaml
logging:
  level:
    com.example.userservice.client: DEBUG
```
```java
@Bean
public Logger.Level feignLoggerLevel() {
    return Logger.Level.FULL;
}
```
해당 설정으로 `Feign Client`관련 로그들을 출력 가능하다.
![](https://i.postimg.cc/mrRxJpbg/2024-09-08-15-37-43.png)
  
  </div>
</details>

<details>
  <summary>part 4 / Feign Client Error Decoder</summary>
  <div markdown="1">
  
`Feign` 패키지에서 지원하는 `Error Decoder`인터페이스를 이용하여 예외 처리    
해당 인터페이스에 포함된 `Decode`는 `Feign Client`에서 발생했던 에러를 상태코드 값을 이용하여 분기되어진 적절한 코드들을 갖고 작업하도록 도와준다.   



  
  </div>
</details>

_토글_

```html
<details>
  <summary>part</summary>
  <div markdown="1"></div>
</details>
```
