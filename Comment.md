

# Section 1
<details>
<summary>part 1 / Erueka 서버 프로젝트 생성</summary>
<div markdown="1">

##  Created erueka server project

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
<summary>part </summary>
<div markdown="1">

API Gateway Service의 역활에 대해서    

해당 서비스는 사용자가 정의한 라우팅 설정에 따라서 각각의 엔트포인트로 클라이언트를 대신하여 요청하고 응당을 받으며 다시 클라이언트한테 전달해주는 프록시 역활을 하게된다.

시스템 내부 구조는 숨기고 외부의 요청에 대하여 적절한 형태로 응답을 하도록 한다는 장점이 있다.

![](https://i.postimg.cc/26m9zyQx/APIGateway2.png)


마이크로 서비스가 3가지 있다고 가정을 하자.

기존에는 모바일나 웹이거나 클라이언트 측에서 마이크로 서비스의 주소를 직접 이용해서 파리미터를 전달하고  요청하는 것으로 볼 수 있다. 여기서 하나의 서비스가 추가되거나, 기존 서비스의 주서가 변경 되거나 등 일이 얼어났을 때 마이크로 서비스가 독립적으로 빌드와 배포가 된다. 그러면 문제는 클라이언트 사이드에서 발생한다.

클라이언트 사이드에서 직접적으로 엔트포인트를 이용해서 호출했을 경우에는 클라이언트 사이드에 있는 어플리케이션 또한 같이 수정, 배포를 해줘야 한다. 

이렇게 단일 진입점을 갖고 있는 형태로의 개발이 필요하게 되었다.

![](https://i.postimg.cc/2yGspvgv/APIGateway.png)

위 처럼 서버에 Gateway역활을 수행을 할 진입점을 하나 두고 각각의 마이크로 서비스로 요청되는 모든 정보에 대하여 일괄적으로 처리할 수 있게 만들어 준다.

어떤 방식에 있어서 직접적으로 마이크로 서비스를 호출하지 않고, 클라이언트는 Gateway만을 상대하게 한다.

이러한 `API Gateway`를 이용하게 된다면 
 * 인증 및 권한 부여에 대한 단일 작업 가능
 * 마이크로 서비스의 검색을 통합 
 * 응답할 수 있는 캐싱 정보를 저장
 * 정책, 회로 차단기 및 Qos 다시 시도
 * 속도 제한과 로드밸런싱
 * 부하 분산 
 * 로깅, 추적, 상관관계
 * 헤더, 쿼리문 문자열 및 청구 반환
 * IP에 대하여 허용, 차단 목록 관리 

## Netflix Ribbon
스프링 클라우드에서 마이크로 서비스간의 통신, 하나의 마이크로 서비스에서 다른 마이크로 서비스를 호출하기 위한 방법 중 대표적인 방법은 `Rest Template`와 `Feign Client`가 있다.

* Rest Template
`Rest Template`은 전통적으로 하나의 웹 어플리케이션에서 다른 어플리케이션을 이용하기 위해서 사용한 방법이다.

```java
RestTemplate restTemplate = new RestTemplate();
restTemplate.getForObject(http://localhost:8080/",User.class,200);
```

* Feign Client
스프링 클라우드에서는 `Feign Client`라는 API를 이용하여 호출이 가능하다.


### API Gateway Service

`API Gateway Service`는 사용자가 설정한 라우팅 설정에 따라 각각의 엔드 포인트로 클라이언트를 대신 요청하고 응답을 받으면 다시 클라이언트 한테 전달해주는 프록시 역활을 한다.


</div>
</details>



_토글_   
```html
<details>
<summary>part </summary>
<div markdown="1">



</div>
</details>
```