# Section 6

## Configuration Srvice

각각의 마이크로 서비스가 갖고 있어야 하는 구성 정보파일로서 YAML파일을 사용 했는데, 해당 파일의 내용이 변경이 된다면 어플리케이션 자체가 다시 빌드되고 배포가 되었다.

어플리케이션 내부에서 구성 파일을 갖고 있는 것이 아닌 외부에 있는 시스템을 바탕으로 구성 파일 정보를 관리할 수 있는 기능에 대해서 확일 할 예정이다.

또한, 구상 파일을 관리함에 있어 기본적인 FTP라든가 네트워크 파일 시스템이라든가 로컬 파일 시스템 또는 형상관리 툴로 많이 사용되는 로컬 git 레파지토리가 있을텐데 git레파지토리 라든가 git레파지토리 그리고 네이티브 파일 시스템의 레파지토리 시스템을 이용하여 적용 시킬 예정

그리고 Spring Boot가 제공하는 Actuator라는 기능을 이용하여 구성 정보 파일을 확인을 한 번 하고 다양한 서비스 내용을 살펴 볼 것이다.

마지막으로 구성정보 파일을 하나만 만들어서 사용하는 것이 아닌 마이크로 서비스가 개발이되고 운영이 되는 각 단계에 맞춰서 설정 파일을 바꿔서 사용이 가능한 프로파일을 만드는 것을 확인 할 것이다.

<details>
  <summary> Spring CLoud Config</summary>
  <div markdown="1">

## Spring Cloud Config

- 분산 시스템에서 서버, 클라이언트 구성에 필요한 정보(application.yml)를 외부에서 관리
- 하나의 중앙화 된 저장소에서 구성 요소 관리 가능
- 각 서비스를 재빌드를 하지 않고, 즉시 적용이 가능
- 어플리케이션 배포 파이프 라인을 통하여 `DEV-AUT-PROD`환경에 맞는 구성 정보 사용
  - 각각의 환경마다 사용하는 환경 설정이 다를 수 있다. 예를 들어 데이터베이스 정보, 게이트웨이의 IP 주소, 테스트를 위한 어떤 특정한 값이라든가 환경마다 다르게 가질 수 있다.
    여기서 다르게 가질 수 있다라는 것은 각 환경에 따라 자유로운 변경이 가능해야 한다.

![](https://i.postimg.cc/LXVMBFTQ/1-Sck-Da-Xx-M3o9nds3-FZMZIz-Q.webp)

  </div>
</details>

<details>
  <summary>part 1 / Created Config service Project </summary>
  <div markdown="1">

해당 파일은 설정하고 push는 하지 않을 생각이다. 왜냐하면 해당 파일의 설정 내용은 로컬에서 한정적으로 관리하기 위해서다.

프로젝트 생성 후 어플리케이션 파일에 `@EnableConfigServer` 어노테이션을 추가한다.  
해당 어노테이션은 Spring Cloud Config Server를 활성화하기 위한 설정이다. 해당 어노테이션을 사용하면 애플리케이션이 외부 Git, SVN, 파일 시스템 등에서 구성 정보를 가져와서 클라이언트 애플리케이션에 제공하는 역할을 한다.  
즉, 분산 환경에서 중앙 집중식으로 설정을 관리할 수 있게 해준다.

yaml설정 파일로 이동하여

```yaml
spring:
  application:
    name: config-service
  cloud:
    config:
      server:
        git:
          uri: file:///C://Users//ljy53/Desktop/git/git-local-repo
```

이 처럼 폴더 위치를 지정을 해주고 프로젝트를 실행을 해준다.  
[ecommerce.yml](http://localhost:8888/ecommerce/default)확인을 하면 설정 코드가 나타날 것이다.

여ꈰ서 `ecommerce/default`로 접속을 하였다. 추가적으로 `ecommerce/test`로 접속을 하게되면 같은 파일의 내용을 보여준다.  
이는 test라는 프로파일이 없기 때문에 default를 보여준 것이다.

  </div>
</details>

<details>
  <summary>part 2 / Apply Spring Cloud Config functionality to User-Service</summary>
  <div markdown="1">
  
  이제 User Microsevice에서 사용하기 위해서 Dependencies를 추가 (spring-cloud-starter-config, spring-cloud-starter-bootstrap) 그리고 `spring.cloud.bootstrap.enable=true`설정을 해준다.

`application.yml`보다 우선 순위가 높은 `bootstrap.yml`파일을 생성한다.

```yaml
spring:
  cloud:
    config:
      uri: http://127.0.0.1:8888
      name: ecommerce
```

원래 갖고 있는 yml파일의 특정한 부분을 떼어서 별도의 공용 서버 같은 역활을 해주는 Spring Cloud Config 서버를 이용하겠다는 것이 목적이다.  
그래서 해당 부분을 떼어서 별도로 따로 저장을 시켰는데 그러려면 해당 부분이 읽어지는 부분들을 `application.yml`파일 보다 먼저 작업을 해야지만 전체적으로 우리가 필요했던 모든 리소스가 맞아 떨아진다.  
따라서 `Spring Cloud Cofig`에 대한 정보를 먼저 등록해 줄 수 있는 파일이 필요하다. 해당 역활을 해주는 것이 `bootstrap.yml`파일을 등록 하므로 외부에 있는 컴퓨터 서버의 정보 파일을 등록해주는 작업이다.

---

이제 User-Service프로젝트에 토큰관련 정보들은 주석처리를 할 것이다. 그 이유는 이제 해당 정보를 `Spring Cloud Config`를 통하여 정보를 갖고 올 것이기 때문이다.

`pom.xml`으로 이동하여 2개의 Dependencies를 추가를 하고 프로젝트를 실행하면  
![](https://i.postimg.cc/Hn0Xkrcz/config2.png)  
이 처럼 comfiguration 서버의 위치, 이름, 위치정보 또한 잘 나타내준다.  
그리고 health_check 메소드를 변경하여 정보를 잘 갖고 오는지 확인하기 위해서 변경했기에 확인을 하면 정보를 잘 갖고온다.

여기서 만약 Configuration의 정보를 변경하게 된다면 다시 갖고와야 한다.  
정보를 다시 갖고 올려면 서버를 재 기동을 하거나, Spring Boot의 `Actuator`기능을 사용하는 방법이다. `Actuator`의 `Refresh`라는 기능을 사용하면 재부팅을 하지 않은 상태에서도 필요한 정보를 얻을 수 있다.  
마지막 방법으로는 `Spring Cloud Bus`를 사용하는 방법이 있다. 해당 방법은 `Actuator`를 사용하는 것 보다 훨씬 더 효율적으로 정보를 갖고 올 수 있다.

일단 먼저 `Actuator`을 사용하는 방법을 살펴 볼 것이다.  
Spring Boot의 `Actuator`라는 것은 어플리케이션의 상태라든가 어플리케이션을 모니터링을 할 수 있는 작업을 이야기한다.

별도의 어플리케이션을 기동하지 않더라도 단순히 Dependency를 추가하여 각종 Metric, 지표, 수치를 수집하기 위한 엔드포인트를 제공해준다.

`Actuator`관련된 모든 코드는 `/actuator`로 시작을 한다. 이러한 정보들은 로그인을 거치지 않고도 사용하기 위해 `permitAll`속성을 추가해준다.  
그리고 아래와 같이 추가적으로 작성을 해준다.

```yaml
management:
  endpoints:
    web:
      exposure:
        include: refresh, health, beans
```

  </div>
</details>

<details>
  <summary>part 3 /  Apply Spring Cloud Config functionality to API Gateway-Service</summary>
  <div markdown="1">

이번에는 API Gateway 프로젝트에 전에 설정한 내용을 추가를 한다.  
이 프로젝트에서는 나머지는 다 비슷하게 작성을 했지만, 전과는 다르게 `Actuator`에서 `httptrace`기능을 사용하기 위해 추가적으로 작성을 해준다.

> Spring 3버전에서 부터 `Actuator`의 `httptrace`는 `httpexchanges`로 변경되었다.

```yaml
- id: user-service
  uri: lb://USER-SERVICE
  predicates:
    - Path=/user-service/actuator/**
    - Method=GET,POST
  filters:
    - RemoveRequestHeader=Cookie
    - RewritePath=/user-service/(?<segment>.*), /$\{segment}
```

  </div>
</details>

<details>
  <summary>part 4 / Set configuration information differently in a multi-environment</summary>
  <div markdown="1">
  
  각각의 마이크로 서비스에서 구성 정보를 다르게 설정하여 실행을 해보겠다.

각 환경에서 사용할 yaml파일을 생성 한 뒤 이름 뒤에 어떤 환경에 사용을 할 것인지 명시를 해준다. 그리고

```yaml
spring:
  profiles:
    active: dev #(or test, prod, ...)
```

`active` 부분에 어떤 환경의 구성 파일을 사용을 할 것인지 명시해준다.

  </div>
</details>

_토글_

```html
<details>
  <summary>part</summary>
  <div markdown="1"></div>
</details>
```
