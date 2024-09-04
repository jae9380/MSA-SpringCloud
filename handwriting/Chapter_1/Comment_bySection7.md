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

#### step 1. ERLANG

[RabbitMQ](https://www.rabbitmq.com/)

일단 먼저 `brew`를 업데이트 해준다.

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

결국 두 환경에서 `RabbitMQ` 실행에 있어 문제가 발생해서 Docker 컨테이너로 생성하여 실습을 진행하기로 결정

```docker

# docker network create --gateway 172.18.0.1 --subnet 172.18.0.0/16 ecommerce-network
docker network create --gateway 172.19.0.1 --subnet 172.19.0.0/16 ecommerce-network
# 172.18 에서 172.19로 변경

docker run -d --name rabbitmq --network ecommerce-network \
-p 15672:15672 -p 5672:5672 -p 15671:15671 -p 5671:5671 -p 4369:4369 \
-e RABBITMQ_DEFAULT_USER=guest \
-e RABBITMQ_DEFAULT_PASS=guest rabbitmq:management
```

  </div>
</details>

<details>
  <summary>part 3 / AMQP</summary>
  <div markdown="1">
  
  Spring Cloud Bus를 사용하기 위해서 각각 dependency를 추가를 한다.   
  * Config Server - AMQP for Spring Cloud Bus, Actuator   
  * Users Microservice, Gateway Service - AMQP for Spring Cloud Bus
  
`RabbitMQ`컨테이너를 실행을 하고    
```yaml
  rabbitmq:
    host: 127.0.0.1
    port: 5672
    username: guest
    password: guest
```
이렇게 연결을 하기 우ㅣ해 설정을 해준다.

이후 `busrefresh`의 동작을 하기 위해서 기존 yml파일에서 토큰에 #1을 추가로 작성하여 기존의 값과 다르게 변경을 하였다. 이후 해당 포트로 접속하여 변경된 부분이 잘 작용되었는지 확인한다.

토큰관련하여 변경된 부분이 잘 적용이 되었는데 아직 프로젝트 내에서는 기존의 변경된 시점 이전의 토큰을 이용하고 있다. 이러한 부분을 해결하기 위해서는 `busrefresh`동작을 해보겠다.

`127.0.0.1:8000/user-service/actuator/busrefresh , POST` 날려보겠다.

해당 동작을 완료하게 되면 User-Microservice와 Gateway-Service에 콘솔을 확인하면

```
2024-09-04T11:37:23.424+09:00  INFO 21256 --- [user-service] [foReplicator-%d] com.netflix.discovery.DiscoveryClient    : DiscoveryClient_USER-SERVICE/user-service:54b4246e40d227085b6a89a2ef984785 - registration status: 204

2024-09-04T11:37:23.519+09:00  INFO 24340 --- [apigateway-service] [nfoReplicator-0] com.netflix.discovery.DiscoveryClient    : DiscoveryClient_APIGATEWAY-SERVICE/DESKTOP-UBM6CI1.mshome.net:apigateway-service:8000 - registration status: 204
```

두 프로젝트에 이렇게 문구가 출력이 된다.

그 이유는 User-Microservice에 변경되었다고 알려주면 `RabbitMQ`에 연결되어 있는 다른 클라이언트 모든 곳에 해당하는 메세지가 푸시 기능으로 전달되었기 때문이다.

그래서 기존 토큰값으로 이용을 하면 변경된 토큰 정보를 사용하지 않았기 때문에 이증에는 실패가 된다.  
(이러면 변경된 정보를 잘 갖고오는 것이다.)

이렇게 Spring Cloud Bus를 이용하면 여러개의 마이크로 서비스가 있다고 하더라도 단 한 번의 `Refresh`로 여러개의 마이크로 서비스가 있다 하더라도 한 번에 `Refresh`가 가능하게 된다.

  </div>
</details>

_토글_

```html
<details>
  <summary>part</summary>
  <div markdown="1"></div>
</details>
```
