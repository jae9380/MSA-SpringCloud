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
16:40
  </div>
</details>

_토글_

```html
<details>
  <summary>part</summary>
  <div markdown="1"></div>
</details>
```
