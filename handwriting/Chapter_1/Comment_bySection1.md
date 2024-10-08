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


_토글_

```html
<details>
  <summary>part</summary>
  <div markdown="1"></div>
</details>
```
