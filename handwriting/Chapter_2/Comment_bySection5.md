# Section 5

장애 처리와 Microservice 분산 추척

여러개의 마이크로 서비스로 나누어서 개발되다 보니 각각의 서비스에 문제가 생겼을 때 어떻게 처리를 해야하는지, 어떤 서비스가 문제가 발생 했는지, 해당하는 마이크로서비스의 시작점은 어디고 그 다음에 끝났을 때 반환값을 누구한테 전달하는지 등 전체적인 흐름을 추척하는 것은 중요한 부분이다.

회복성 패턴이라 불리는 `CircuitBreaker`, `Resilience4j`, 분산 추척에 대하여 알아보고

분산추적을 할 때 `TraceID` 와 `SpanID`를 알이보고, `Zipkin serever`를 이용하여 마이크로 서비스에 발생되었던 로깅 정보, 트래킹 정보를 저장할 수 있는 기능을 확인

 <details>
  <summary>part 1 / CircuitBreaker, Resilience4j 사용하기 </summary>
  <div markdown="1">

`CircuitBreaker`은 간단하게 장애가 발생하는 서비스에 반복적인 호출이 되지 못하게 차단을 한다. 그리고 특정 서비스가 정상적인 동작을 하지 않을 경우 다른 기능으로 대체 수행을 하도록 만들어 장애에 대해서 회피가 가능하다.

```java
List<ResponseOrder> ordersList = orderServiceClient.getOrders(userId);
/* 기존 코드에서 아래와 같이 변경 */
CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitBreaker");
List<ResponseOrder> ordersList = circuitBreaker.run(() -> orderServiceClient.getOrders(userId), throwable -> new ArrayList<>());
```

`CircuitBreaker`패턴을 사용하여 `orderServiceClient.getOrders(userId)`을 호출 할 때 장애 발생 시 대체 동작을로 비어있는 리스트를 반환하도록 설정을 했다.

  </div>
</details>

<details>
  <summary>part 2 / 분산 추적</summary>
  <div markdown="1">
  
연쇄적으로 여러 서비스가 실행될 때 과정에 요청 정보가 어떻게 실행이 되고 어느 단계를 거치는지 추적을 하기 위해서 분산 추적을 할 것이다.   
분산 추적을 하기 위해서는 트레이싱 정보를 저장하기 위해 `Zipkin`이라는 서버를 사용을 한다.

> ## Zipkin
>
> - Span
>   하나의 요청에 사용되는 작업의 단위이다.
>   이러한 `span`은 고유한 Id가 하나가 부여가 되며, 이러한 `span`이 모야서 하나의 `Trace`가 된다.
> - Trace
>   트리 구조로 이루어진 `sapn`셋
>   하나의 요청에 같은 `Trace ID`발급

## Spring Cloud Sleuth

`Zipkin`과 연동을 하여 갖고있는 로그 파일의 데이터나 스트리밍 데이터를 `Zipkin`에 전달하는 역활을 한다

```properties
<!-- Spring 3. 이전 버전 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-sleuth</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-zipkin</artifactId>
            <version>2.2.3.RELEASE</version>
        </dependency>

<!-- Spring 3. 이후 버전 -->
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-observation</artifactId>
        </dependency>

        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-tracing-bridge-brave</artifactId>
        </dependency>

        <dependency>
            <groupId>io.zipkin.reporter2</groupId>
            <artifactId>zipkin-reporter-brave</artifactId>
        </dependency>
```

```yaml
# Spring 3. 이전 버전
spring:
  zipkin:
    base-url: http://127.0.0.1:9411
    enabled: true
  sleuth:
    sampler:
      probabaility: 1.0

# Spring 3. 이후 버전
spring:
  zipkin:
    base-url: http://127.0.0.1:9411
    enabled: true

management:
  tracing:
    sampling:
      probability: 1.0
    propagation:
      consume: b3
      produce: b3_multi
  zipkin:
    tracing:
      endpoint: "http://localhost:9411/api/v2/spans"
```

해당 설정들을 해주고 User, Order 서비스를 기동하여 상품을 등록하고 로그를 보면

```
[order-service] [o-auto-1-exec-1] [66f3cd53a15101d2781851112c224fdd-781851112c224fdd]
```

이와 같이 출력되었을 것이다.  
`66f3cd53a15101d2781851112c224fdd-781851112c224fdd` 이 부분에서 `-`를 기준으로 앞의 값음 `TraceID`이고 뒷 부분은 `SpanID`를 나타낸다.

  </div>
</details>

<details>
  <summary>part 2 / 모니터링 </summary>
  <div markdown="1">

```xml
        <!-- Micrometer-->
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-registry-prometheus</artifactId>
        </dependency>

```

추가와 `application.properties`에 `info, metrics, prometheus`도 추가적으로 설정을 해준다.

`micrometer`라이브러리에서 `@Timed`라는 어노테이션을 제공한다. 이것을 이용하여 쉽게 counter metrics을 적용할 수 있다. 이것을 이용하기 위해 `spring boot 3.` 버전 이후는 `TimedAspect`타입의 빈을 등록을 해줘야 한다.

```java
@Configuration
public class TimedConfig {
    @Bean
    public TimedAspect timedAspect(MeterRegistry meterRegistry) {
        return new TimedAspect(meterRegistry);
    }
}
```

이후 컨트롤러에서 해당 어노테이션을 사용하여 맵핑을 시도하고, `Actuator`의 `metrics`, `prometheus`로 들어가서 확인을 하면 나타날 것이다.

  </div>
</details>

<details>
  <summary>part 3 / Prometheus + Grafana</summary>
  <div markdown="1">

## Prometheus

`Prometheus`라는 모니터링 도구는 매트릭스를 수집하고 모니터링 및 알람에 사용되고 있는 오픈소스 어플리케이션이다.

`Pull` 방식의 구조와 다양한 `Metric Exporter` 제공을 한다.

[Prometheus 다운로드](https://prometheus.io/download/)

## Grafana

데이터 시각화, 모니터링 및 분석을 위한 오픈소스 어플리케이션이다.  
시계열 데이터를 시각화하기 위한 대시보드를 제공한다.

[Grafans 다운로드](https://grafana.com/grafana/download)

`Prometheus` 디렉토리 내부 `Prometheus.yml`에 추가적인 설정을 할 수 있다.

`scrape_configs:` 아래에 추가적으로

```yaml
- job_name: "user-service"
  scrape_interval: 15s
  metrics_path: "/user-service/actuator/prometheus"
  static_configs:
    - targets: ["localhost:8000"]
```

그리고 , `Order-Service`, `APIGateway-Service` 등 추가적으로 설정,

- Prometheus 실행

  - MacOS - ./prometheus --config.file=prometheus.yml
  - Windows - .\prometheus.exe

- Grafana 실행

  - MacOS - ./bin/grafana-server
  - Windows - .\bin\grafana-server start

    </div>
  </details>

_토글_

```html
<details>
  <summary>part</summary>
  <div markdown="1"></div>
</details>
```
