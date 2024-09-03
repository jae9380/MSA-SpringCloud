# Section 6

## Spring Cloud Bus & RabbitMQ

지금까지 Spring Cloud Confiuration 서버에 대하여 알아봤다.  
각각의 마이크로 서비스가 갖고 있어야 할 구성정보 환경설정 등의 정보를 공통으로 처리하기 위한 Confiuration 서버에 작성하여 정보를 전달하는 방법과 변경사항이 생기면 각각의 마이크로 서비스가 변경된 정보를 갖고오는 것을 확인했다.

이번에는 각각의 마이크로 서비스가 좀 더 효율적인 방법으로 변경된 사항을 갖고오는 Spring Cloud Bus에 대하여 알아보고, RebbitMQ라는 메세징 큐잉 서비스를 하나 추가하여 같이 연동할 계획이다.

<details>
  <summary>part 1 / Spring Cloud Bus</summary>
  <div markdown="1">
  
  Configuration 서버에서 어떤 값이 변경이 되었을 때, 해당 값을 받는 방법은 총 3가지 방법이 있다고 했었다. 서버 재기동, `Actuator`에서 `refresh`기능을 사용하는 방법, 마지막으로 `Spring cloud Bus`사용하는 방법이 있다고 했다.

`Actuator`방법은 어플리케이션이 1~2개 정도묜 큰 문제가 되지 않는다. 하지만 구성된 어플리케이션이 수십, 수백개로 구성되어 있다면 각각의 어플리케이션 마다 `refresh`를 수동으로 해줘야 한다는 단점이 존재한다.

이러한 단점을 개선하기 위하여 `Spring Cloud Bus`를 이용할 것이다.  
해당 기술을 사용하면 분산 시스템의 노드를 경량 메세지 브로커와 연결, 상태 및 구성에 대한 변경사항은 연결된 노드에게 전달 (Broadcast), (여기서 말하는 노드는 마이크로 서비스라고 생각하면 된다.)

![](https://i.postimg.cc/rsL6QfTF/image.png)

외부에서 별도의 클라이언트가 `POST` 방식으로 Busrefresh라는 `Actuator`을 호출을 할 것이다.  
(여기서 호출되는 위치는 별로 중요하지가 않다. Spring Cloud Bus에 연결된 누구에게 호출을 하게되면 해당 연결된 노드들에게 똑같이 전달을 해주게 된다.)

  </div>
</details>

<details>
  <summary>part 2 / install RabbitMQ</summary>
  <div markdown="1">
  
## MAC OS

## Windows

윈도우 환경에서 `rabbitMQ`를 설치하기 위해서는 `Erlang`을 먼저 설치를 해야 한다.

#### step 1. ERLANG

[Erlang](https://www.erlang.org/downloads)으로 접속하여 다운을 받고 환경변수에 등록을 해준다.

#### step 2. RabbitMQ

[RabbitMQ](https://www.rabbitmq.com/docs/download)으로 접속하여 자신의 환경에 맞게 다운로드를 받고 또한 환경변수에 등록을 한다.  
(Windows 부분에 installer로 다운을 받았다.)

이후 추가적으로 Management를 다운을 받기 위해서 power shell을 열어서

```shell
rabbitmq-plugins enable rabbitmq_management
```

입력하여 매니지먼트 다운 진행

http://127.0.0.1:15672 로 접속을 해보면 RabbitMQ가 나타날 것이다.

- 만약 RabbitMQ가 나타나지 않을 경우

  </div>
</details>

_토글_

```html
<details>
  <summary>part</summary>
  <div markdown="1"></div>
</details>
```
