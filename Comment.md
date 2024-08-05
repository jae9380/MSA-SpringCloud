# Section 1

<details>
<summary>part 1 / Erueka 서버 프로젝트 생성</summary>
<div markdown="1">

## Created erueka server project

`@EnableEurekaServer`
선언된 어플리케이션을 Eureka 서버로 설정할 때 사용  
해당 어노테이션을 사용하면, 해당 애플리케이션은 Eureka 서버로서 동작한다.
Eureka 서버는 다른 마이크로서비스 인스턴스의 등록과 디스커버리를 관리를 한다.

```yaml
eureka:
  client:
    register-with-eureka: false # or true
    fetch-registry: false # or true
```

`register-with-eureka`
일반적으로 Eureka 서버 자체를 설정할 때 사용을 한다.  
Eureka 서버는 다른 마이크로서비스 인스턴스를 관리하고 등록하기 때문에 지금 프로젝트는 서버 자체가 다시 자기 자신에게 등록될 필요가 없기에 `false`로 설정

`fetch-registry`
Eureka 서버를 설정할 때 주로 사용됩니다.  
Eureka 서버는 자신이 등록된 인스턴스 정보를 가지고 있어야 하며, 다른 서버로부터 정보를 가져올 필요가 없기 때문에 `false`로 설정을 함

## 실행 화면

![](https://i.postimg.cc/KjVShF6G/2024-07-23-18-24-55.png)

---

</div>
</details>

<details>
<summary>part 2 / Erueka 클라이언트 프로젝트 생성</summary>
<div markdown="1">

## Created eureka client project

[user-service 프로젝트 생성](https://github.com/jae9380/user-service)  
해당 프로젝트는 Eureka 서버에 등록 할 클라이언트 프로젝트이기 때문에 part 1 에 yml설정 부분에서 Eureka 설정 값을 false 👉true 변경하여 작성  
그리고 url추가를 위해서 작성

```yaml
service-url:
  defaultZone: http://127.0.0.1:8761/eureka
```

클라이언트 프로젝트 부분에 `@EnableDiscoveryClient` 추가

이후 서버 프로젝트와 클라이언트 프로젝트를 실행하여 서버에 접속을 하면

![](https://i.postimg.cc/sfWyWHZt/2024-07-23-19-17-20.png)

위와 같이 서버에 클라이언트가 추가된 덧을 확인할 수 있다.

---

</div>
</details>

<details>
<summary>part 3 /  클라이언트 프로젝트 실행 </summary>
<div markdown="1">

## Porject testing

클라이언트 프로젝트 3개 실행시키기
포트번호를 각 1, 2, 3으로 설정
(3번의 경우, 터미널에서 아래 명령어 입력)

```cmd
mvn spring-boot:run -Dspring-boot.run.jvmArguments='-Dserver.port=9003'
```

![](https://ifh.cc/g/NzsrA9.jpg)
클라이언트 3개가 잡혀있는 것을 확인할 수 있다.

추가적으로 하나 더 실행을 하겠다.

이번에는 바탕화면에서 터미널을 이용을 한다.
`cd` 명령어를 입력하여 프로적트가 있는 위치로 이동 후

```cmd
java -jar -Dserver.port=9004 ./target/user-service-0.0.1-SNAPSHOT.jar
```

</div>
</details>

<details>
<summary>part 4 / 포트 관련 설정</summary>
<div markdown="1">

## YML file configuration for setting the port number of the client project

기존의 클라이언트의 `application.yml`파일에서 서버 포트를 지정을 했을 때
해당 프로젝트를 중복 실행을 위해서 직접 포트를 다른 값으로 지정을 하였다.
하지만 이러한 방법은 효율적이지 못 하다.

그래서 포트의 값을

```yaml
server:
  port: 0

eureka:
  instance:
    instance-id: ${spring.cloud.client.hostname}:${spring.application.instance_id:${random.value}}
```

변경하여 포트 번호를 랜덤으로 지정하는 방법으로 수정

![](https://i.postimg.cc/cLxndydV/2024-07-26-132353.png)
![](https://i.postimg.cc/N0L2r69D/2024-07-26-133542.png)
![](https://i.postimg.cc/g05Xyx3W/2024-07-26-133603.png)

[URL of the commit for the client project](https://github.com/jae9380/user-service/commit/e8ce0d8fb1ef0df3f89339ce1dab67ff9453e14d)

</div>
</details>

# section 2

<details>
<summary>part 1 / API Gateway, Netflix Ribbon </summary>
<div markdown="1">

## Role of API Gateway Service

해당 서비스는 사용자가 정의한 라우팅 설정에 따라서 각각의 엔트포인트로 클라이언트를 대신하여 요청하고 응당을 받으며 다시 클라이언트한테 전달해주는 프록시 역활을 하게된다.

시스템 내부 구조는 숨기고 외부의 요청에 대하여 적절한 형태로 응답을 하도록 한다는 장점이 있다.

![](https://i.postimg.cc/26m9zyQx/APIGateway2.png)

마이크로 서비스가 3가지 있다고 가정을 하자.

기존에는 모바일나 웹이거나 클라이언트 측에서 마이크로 서비스의 주소를 직접 이용해서 파리미터를 전달하고 요청하는 것으로 볼 수 있다. 여기서 하나의 서비스가 추가되거나, 기존 서비스의 주서가 변경 되거나 등 일이 얼어났을 때 마이크로 서비스가 독립적으로 빌드와 배포가 된다. 그러면 문제는 클라이언트 사이드에서 발생한다.

클라이언트 사이드에서 직접적으로 엔트포인트를 이용해서 호출했을 경우에는 클라이언트 사이드에 있는 어플리케이션 또한 같이 수정, 배포를 해줘야 한다.

이렇게 단일 진입점을 갖고 있는 형태로의 개발이 필요하게 되었다.

![](https://i.postimg.cc/2yGspvgv/APIGateway.png)

위 처럼 서버에 Gateway역활을 수행을 할 진입점을 하나 두고 각각의 마이크로 서비스로 요청되는 모든 정보에 대하여 일괄적으로 처리할 수 있게 만들어 준다.

어떤 방식에 있어서 직접적으로 마이크로 서비스를 호출하지 않고, 클라이언트는 Gateway만을 상대하게 한다.

이러한 `API Gateway`를 이용하게 된다면

- 인증 및 권한 부여에 대한 단일 작업 가능
- 마이크로 서비스의 검색을 통합
- 응답할 수 있는 캐싱 정보를 저장
- 정책, 회로 차단기 및 Qos 다시 시도
- 속도 제한과 로드밸런싱
- 부하 분산
- 로깅, 추적, 상관관계
- 헤더, 쿼리문 문자열 및 청구 반환
- IP에 대하여 허용, 차단 목록 관리

## Netflix Ribbon

스프링 클라우드에서 마이크로 서비스간의 통신, 하나의 마이크로 서비스에서 다른 마이크로 서비스를 호출하기 위한 방법 중 대표적인 방법은 `Rest Template`와 `Feign Client`가 있다.

- Rest Template
  `Rest Template`은 전통적으로 하나의 웹 어플리케이션에서 다른 어플리케이션을 이용하기 위해서 사용한 방법이다.

```java
RestTemplate restTemplate = new RestTemplate();
restTemplate.getForObject(http://localhost:8080/",User.class,200);
```

- Feign Client
  스프링 클라우드에서는 `Feign Client`라는 API를 이용하여 호출이 가능하다.

```java
@FeignClient("stores")
public interface StoreClient {
  @RequestMapping(method = RequestMethod.GET, value="/stores")
  List<Store> getStores();
}
```

위 처럼 특정한 인터페이스를 생성을 하고, 웹으로 따로 호출하고 싶은 추가적인 마이크로 서비스의 이름을 등록을 한다.

직접적인 서버의 주소, 포트 번호 없이 마이크로 서비스의 이름을 갖고 호출할 수 있게된다.

이러한 방법으로 스프링 클라우드에서 마이크로 서비스 간의 호출을 담담했는데, 문제는 로드 발랜서를 하기 위해서 어디에 구축해서 작업을 할 것인가가 문제이다.
초기 스프링 클라우드에서는 이러한 로드 발랜서의 역확을 담당하는 별도의 서비스 프로젝트를 위해 Ribbon이라는 서비스를 제공하기 시작했다.

> Ribbon : Client side Load Balancer

그런데 리본이라는 방식은 최근 `functional API` 또는 `React JAVA`라고 하는 방식과는 호환이 많이 안되는 방식 즉, 비동기가 처리가 잘 되지 않는 방식이기 때문에 최근에 이러한 방식을 사용하지 않는다.

그리고 `Health Check`라고 해서 해당하는 서비스가 정삭적인 동작을 하는지 확인할 수 있다.

마이크로 서비스 4가지가 있다고 했을 때, 클라이언트와 서비스 사이에 `API Gateway`를 중간에 두고 동작을 해야하는데 이러한 구조가 아닌 클라이언트 내부에 `Ribbon`이라는 서비스를 구축하여 사용을 하기 시작했다. (Client Side에 위치하고 있다.)

![](https://i.postimg.cc/9f7Cmx0H/img.png)

클라이언트 프로그램 내부에서 이동하고자 하는 마이크로 서비스의 주소 값을 직접 관리를 하는 구조다.

Client Side Load Balancer 장점으로는 IP하고, 포트 번호를 명시하는 방식이 아닌 그냥 단순히 서비스의 이름만 갖고 호출이 가능하다는 장점이 있다.

## Netflix Zuul

`Netflix Zuul`은 방금 전 확인했던 Gateway을 담당해주는 제품이다.

![](https://i.postimg.cc/WzfHJvYW/Zuul.jpg)

</div>
</details>

<details>
  <summary>part 2 / Netflix Zuul Test</summary>
  <div markdown="1"></div>

간단한 내용을 담고있는 두 가지의 서비스를 만들어 Zull이라는 Gateway에서 두 가지의 사용자의 요청이 왔을 때, 각 서비스로 잘 분산되는지 확인을 할 것이다.

[Zuul](https://github.com/jae9380/MSA-SpringCloud/tree/main/zuul-service)
[서비스 1](https://github.com/jae9380/MSA-SpringCloud/tree/main/first-service)
[서비스 2](https://github.com/jae9380/MSA-SpringCloud/tree/main/second-service)

일단 각 서비스는 각 포트에 맞는 url로 접속을 하면 간단한 문구가 출력이 되는 서비스로 구성
![](https://i.postimg.cc/MGngpjPj/2024-07-31-15-49-11.png)

다음으로 Zuul 프로젝트 생성

해당 프로젝트 메인 파일에 `@EnableZuulProxy`어노테이션 설정  
(해당 어노테이션을 설정을 하면 Zuul 프록시 서버로서의 동작을 가능하게 한다.)

![](https://i.postimg.cc/8zPqxYL4/2024-07-31-15-56-07.png)
위 처럼 yml파일에 연결할 서비스의 이름과 해당 서비스의 path, url을 설정을 하고 줄 서비스로 각 서비스로 접근을 해보자

![](https://i.postimg.cc/FKkn4Xzy/2024-07-31-15-59-18.png)  
그러면 이 처럼 문구가 잘 나타난다.

이렇게 간단한 예제를 사용하여 API Gateway 라우팅 기능을 확인했다.

</details>

<details>
  <summary>part 3 / Logging filter</summary>
  <div markdown="1"></div>

Zull fliter를 사용하기 위해서 추상클래스 ZullFiler를 상속 받고 추상 메소드를 정의하여 요청될 때 로그를 남기게 할 것이다.
![](https://i.postimg.cc/tC2X1W5r/2024-07-31-16-20-22.png)

그러면 각 메소드는 어떤 기능을 하고 어떻게 설정하는지에 대하여 알아보겠다.

- boolean shouldFilter()  
  해당 메소드는 필터의 실행 여부를 결정하는 메소드이다.

- Object run()  
  필터의 주요한 로직을 구성하는 메소드이다.  
  주로 요청을 변경하거나, 로그를 남기거나, 응답을 조작할 때 사용을 한다.

- String filterType()  
  필터의 타입을 지정한다. 필터가 어떤 타입을 갖는냐에 따라 언제 실행될지 결정한다.

  필터 타입의 예제

  - pre : 라우팅 전에 실행
  - routing : 실제 라우팅을 수행할 때 실행
  - post : 라우팅 후에 실행
  - error : 오류가 발생했을 때 실행

- int filterOrder()  
  여러개의 필터가 존재할 경우, 필터의 실행 순서를 지정한다.

</details>

<details>
  <summary>part 4 / Spring Cloud API-Gateway test</summary>
  <div markdown="1"></div>

기존에는 Tomcat이라는 서버가 작동되었을 것이다. 하지만 이번에는 Netty서버가 동작을 한다.  
 Zuul에서는 동기 방식인 Tomcat 서버를 사용을 했지만, Spring Cloud API-Gateway를 사용하면 비동기 방식인 Netty가 작동을 한다.

![](https://i.postimg.cc/jdDQDQ6c/2024-07-31-16-50-50.png)

</details>

<details>
  <summary>part 5 / Spring Cloud Gateway Filter</summary>
  <div markdown="1"></div>
  Gateway에서의 Filter는 어떻게 동작이 되는지 확인하겠다.

![](https://i.postimg.cc/DwkFWh4z/Filter.png)

클라이언트가 Gateway에게 요청을 전달하면, Gateway에서 어떤 서비스로 이동을 할 것인가 판단을 하고 이동을 한다.

</details>

<details>
  <summary>part 6 / Gateway Filter Routing configuration in JAVA code</summary>
  <div markdown="1"></div>

apigateway-service파일 내 yml파일에서 cloud 관련 설정을 주석 처리 후 자바 파일 내부에서 이를 설정을 할 것이다.

![](https://i.postimg.cc/0NvDVyZ3/2024-08-01-20-32-47.png)

위코드에서 `r.path` 부분의 값의 요청이 들어오면 `uri`부분으로 이동을 한다.  
그리고 필터 부분에서 요청과, 응답으로 두 가지로 등록을 할 수 있다.  
헤더 부분에 어떠한 값을 저장할려고 하면 `K-V`형태로 저장을 하면 된다.

( 위 코드에서 `.addRequestHeader`가 중복되어 있는데 아래의 코드를 `.addResponseHeader`로 수정)

![](https://i.postimg.cc/VLSq6Td7/2024-08-01-20-32-06.png)

이제 출력이 잘 되는지 확인을 하면
![](https://i.postimg.cc/8zZfmmJF/2024-08-01-20-39-37.png)
![](https://i.postimg.cc/7LcCpPNw/2024-08-01-20-43-53.png)
Request Header와 Response Header가 잘 나타난다.

</details>

<details>
  <summary>part 7 / configuration in Yml file</summary>
  <div markdown="1"></div>

저번 단계에서 자바 코드로 작성한 내용을 다시 yml파일에서 설정을 하기 위해서 기존에 작성한 JAVA코드는 주석처리 후, yml파일에서 주석을 해제를 한다.  
그리고 추가적인 Fliter를 설정하기 위해서
전 단게에서 작성한 Fliter 클래스에 내용 주석 처리 그리고 yml 내용 주석 해제 후

```yaml
filters:
  - AddRequestHeader = first-request, first-request-header2
  - AddResponseHeader = first-response, first-response-header2
```

추가 작성을 하고 Postman으로 테스트를 하면  
![](https://i.postimg.cc/6qS0RWf3/2024-08-02-16-31-03.png)
header값이 잘 나타나는 것을 확인 가능하다.

</details>

<details>
  <summary>part 8 / Custom Filter </summary>
  <div markdown="1"></div>

![](https://i.postimg.cc/Z5xhPnC0/2024-08-02-17-05-56.png)

`AbstractGatewayFilterFactory`를 상속을 받은 후 메소드를 재정의 하여
간단하게 로그를 출력하는 필터를 설정하였다.

이후 yml파일로 가서 기존의 작성한 필터 내용을 제거를 하고

```ymal
-filters
  -CustomFilter
```

이 처럼 커스텀 필터를 적용하고 실행을 해보면

```
... : Custom PRE filter : Request ID -> 0832b3a0-3
... : Custom POST filter : Response code -> 200 OK

```

이 처럼 콘솔 부분에 의도한 방향대로 문구가 잘 출력 되는 것을 확인 가능하다.

</details>

<details>
  <summary>part 9 / Global Filter </summary>
  <div markdown="1"></div>
  필요한 필터가 있을 경우 CustomFilter처럼 정의를 하면 되는데, 공통적인 필터가 필요할 경우 매번 작성하는 것이 아닌 GlobalFilter를 지정하여 한번에 적용 시킬 수 있다.

작성은 CustomFilter와 유사하게 작성, 그리고 설정은 yaml 파일로 돌아와서 `default-filters`로 설정

```yaml
cloud:
gateway:
  default-filters:
    - name: GlobalFilter
      args:
        baseMessage: Spring Cloud Gateway Global Filter
        preLogger: true
        postLogger: true
```

![](https://i.postimg.cc/FKkTtpZ1/Global-Filter-console.png)

</details>

<details>
  <summary>part 10 / Logging Filter</summary>
  <div markdown="1"></div>

Gateway Client -> Gateway Handler -> Global Filter -> Custom Filter -> Logging Filter -> Proxied Service

이 순서로 동작을 할 것이다.

LoggingFilter 코드 작성 후, 해당 필터 클래스를 적용을 할려고 하면 적용시킬 부분에 추가로 작성을 하면 된다.

First-Service에는 적용하지 않고, Second-Service에 적용을 하였다. 적용을 할 때 하나의 필터만 존재를 할 때 filters 부분에 name을 추가 작성을 하지 않아도 적용이 되지만, 2개 이상의 필터를 적용시킬 경우에는 name을 붙여야만 한다.  
![](https://i.postimg.cc/Lsq2rcwf/Logging-Filter-yaml.png)

콘솔 부분에서 출력된 내용을 보면 기존에 말 했던 실행 순서와는 다른 순서로 출력이 되었따.  
![](https://i.postimg.cc/cLyRgWM1/Logging-Filter-console.png)  
그 이유로는 필터의 우선 순위 설정을 높게 설정되어 있기 때문에 이러한 순서가 나타난 것이다.

```java
 OrderedGatewayFilter.HIGHEST_PRECEDENCE
//  HIGHEST_PRECEDENCE를 LOWEST_PRECEDENCE 설정을 하면 의도한 순서대로 나타날 것이다.
```

</details>

<details>
  <summary>part 11 / Load Balancer</summary>
  <div markdown="1"></div>
  * Eureka 연동   
  Eureka Server는 Service Discovery, Registration 역활을 하게된다.   
  지금까지 8081, 8082 port를 갖는 first, second service가 있을 때,    
   `http://localhost:8000/~~`의 요청이 들어오면 API Gateway를 거쳐 Eureka를 경유하여 해당 서비스의 위치를 전달 받고, Gateway가 해당 서비스로 포워딩을 하게 해준다.

apigateway 프로젝트로 돌아와서 yaml 파일에서 등록한 서비스 설정에서  
 `routes: `에 설정한 `uri`의 값을 `lb://MY-FIRST(SECOND)-SERVICE`로 설정해준다.

위 설정들을 하면 http프로토콜을 이용하여 각 서비스로 가는 것이 아닌 Eureka server로 가서 클라이언트 요청 정보를 전달해주게 된다.

![](https://i.postimg.cc/NFRgv2Dh/load-Balancer.png)  
각 서비스를 2개를 실행시켰다.

이 상황에서 하나의 서비스에 접근 시킬 때, 해당 서비스는 어떠한 포트의 서비스에 접속을 했는지에 대하서는 아직 확인은 불가능하다.

이 부분에 대하여 어떤 방식으로 구동이 되는지 확인하기 위해서 각 서비스의 포트를 랜덤포트로 할당하게 설정을 하고, 해당 서비스를 check로 접속하게 되면 포트번호를 출력하게 설정 한 뒤, 서비스를 2개를 실행하고 접속을 했을 때 처음에는 `A`라는 포트로 접속이 되었고, 페이지를 새로 고쳤을 때 `B`라는 포트로 접속이 되었다. 여기서 다시 새로고침을 하면 이번에는 `A`로 다시 접속하게 되었다.  
이렇게 하운드 라운드 로빈 방식으로 Gateway가 호출을 해주는 것을 확인 가능하다.

</details>

_토글_

```html
<details>
  <summary>part</summary>
  <div markdown="1"></div>
</details>
```
