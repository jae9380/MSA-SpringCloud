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

_토글_

```html
<details>
  <summary>part</summary>
  <div markdown="1"></div>
</details>
```
