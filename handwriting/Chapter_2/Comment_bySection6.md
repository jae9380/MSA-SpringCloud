# Section 6

## Containerize the project using Docker

<details>
  <summary>Config Service</summary>
  <div markdown="1">

## `Configuration Service`를 컨테이너화

```dockerfile
COPY apiEncryptionKey.jks apiEncryptionKey.jks
```

기존에는 로컬에 존재하던 Key를 사용했기 때문에 컨테이너 내부에 해당하는 Key 또한 있어야 하기 때문에 설정을 해줘야 한다.

사용하던 Key파일을 프로젝트 내부에 붙여놓고 `bootstrap.yml`내부에 있는 주소 값 또한 변경을 해줘야 한다.

이전에 있었던 빌드가 있을 경우 해당 파일을 삭제하고 새로 생성, 추가적으로 테스트는 없기 때문에 테스트는 스킵

```bash
$ mvn clean compile package -DskipTests=true

$ docker build -t jae9380/config-service:1.0 .
```

추가적으로 기존에 설정한 `RabbitMQ` 내용을 컨테이너화를 했기 때문에 해당 내용 또한 수정을 해줘야 한다.

물론 해당 프로젝트 내부에 직접 ip 주소를 명시해도 된다. 하지만 해당 주소는 변경이 될 경우가 있을 수 있기 때문에 다른 방법으로 설정을 할 것이다.

해당 방법은 직접적인 주소 명시를 하는 방법이 아닌, 해당 컨테이너의 이름을 명시를 하는 것이다.

```bash
docker run -d -p 8888:8888 --network ecommerce-network -e "spring.rabbitmq.host=rabbitmq" -e "spring.profiles.active=default" --name config-service jae9380/config-service:1.0
```

  </div>
</details>

<details>
  <summary>part 2 / Discovery Service, ApiGateway Service</summary>
  <div markdown="1">

## Discovery Service

`discovery-service`프로젝트 또한 `Configuration-Service`의 방법과 유사한 방법으로 실행하면 된다.

이미지를 만들었다면 이번에는 허브에 등록을 해보겠다.

```bash
$ docker push jae9380/discovery-service:1.0
$ docker push jae9380/config-service:1.0
```

이와 같이 명령어를 사용을 할 때 주의해야 할 부분이 있다. 뒤에 버전을 명시를 해줘야 한다. 만약 버전을 명시하지 않았을 경우에는 `latest`를 검색하게 되어버린다.

## ApiGateway Service

해당 프로젝트에서 설정해야 하는 부분은 크게 `Eureka`정보, `RabbitMQ`정보, `Configuration` 정보를 설정해줘야 한다.

```yml
# Eureka
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka

# Rabbitmq
spring:
  rabbitmq:
    host: 127.0.0.1
    port: 5672

# Configuration
spring:
  cloud:
    config:
      uri: http://127.0.0.1:8888
      name: config-service
```

해당 부분 설정을 해줘야 하기 때문에 아래와 같이 설정을 해준다.

```bash
docker run -d -p 8000:8000 --network ecommerce-network \
	-e "spring.cloud.config.uri=http://config-service:8888" \
	-e "spring.rabbitmq.host=rabbitmq" \
	-e "eureka.client.serviceUrl.defaultZone=http://discovery-service:8761/eureka/" \
	--name apigateway-service jae9380/apigateway-service:1.0
```

  </div>
</details>

<details>
  <summary>part 3 / MariaDB</summary>
  <div markdown="1">
  
이번에는 데이터베이스를 띄울 것이다.

기존에 로컬 환경에서 사용한 데이터들이 있을 것이다. 해당 데이터를 사용을 할 것이기 때문에 기존에 있던 내용들을 잠시 복사

Mac OS 같은 경우 `cp`명령어 사용, Windows 경우는 복사로 진행

```dockerfile
FROM mariadb:[version]
ENV MYSQL_ROOT_PASSWORD test123
ENV MYSQL_DATABASE mydb
COPY ./mysql_data/data /var/lib/mysql
EXPOSE 3306
```

해당 과정에서 이와 같은 에러가 발생할 수 있을 것이다.
`InnoDB: Upgrade after a crash is not supported. The redo log was created with MariaDB 10.5.19. You must start up and shut down MariaDB 10.7 or earlier.`  
이럴 경우에는 로컬에서 사용한 버전을 확인하여 `Dockerfile`에 버전을 명시

```sql
SELECT VERSION();
```

만약 기존의 파일이 필요하지 않는다면 `Dockerfile`내 `COPY`제거 후 실행

컨테이너 생성 후 로그를 확인을 하면 잘 나타날 것이다.

```bash
docker logs mariadb
```

어떤한 IP에서 접근할 수 있도록 설정을 해주자

```sql
grant all privileges on *.* to 'root'@'%' identified by 'test123';

flush privileges;
```

  </div>
</details>

<details>
  <summary>part 4 / Kafka, Zipkin</summary>
  <div markdown="1">

## Kafka, Zookeeper

[githyb by.wurstmeister/kafka-docker](https://github.com/wurstmeister/kafka-docker)
해당 주소를 시용하여 clone을 하여 파일을 사용할 계획이다.

해당 폴더 내 `docker-compose-single-broker.yml`파일을 사용할 것이다.  
내부에 ip 주소값을 수정을 하고, `kafka`부분에 `depends_on` 설정

```yml
depends_on:
  - zookeeper
```

그리고 네트워그 명시를 해준다.

```yml
version: "2"
services:
zookeeper:
  image: wurstmeister/zookeeper
  ports:
    - "2181:2181"
  networks:
    my-network:
      ipv4_address: 172.19.0.100
kafka:
  # build: .
  image: wurstmeister/kafka
  ports:
    - "9092:9092"
  environment:
    KAFKA_ADVERTISED_HOST_NAME: 172.19.0.101
    KAFKA_CREATE_TOPICS: "test:1:1"
    KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
  volumes:
    - /var/run/docker.sock:/var/run/docker.sock
  depends_on:
    - zookeeper
  networks:
    my-network:
      ipv4_address: 172.19.0.101

networks:
my-network:
  external: true
  name: ecommerce-network # 172.19.0.1 ~
```

해당 파일이 있는 위치에서 해당 명령어 사용

```bash
docker-compose -f docker-compose-single-broker.yml up -d
```

## Zipkin

[Zipkin](https://zipkin.io/pages/quickstart) 해당 페이지로 들어가 `Quickstart`내용에 `Docker`로 구동하는 방법이 나와있다.

해당 방법은 굉장히 간단하기 때문에 네트워크 설정만 주의하면 큰 문제는 없을 것이다.

```bash
$ docker run -d -p 9411:9411 \
          --network ecommerce-network \
          --name zipkin \
          openzipkin/zipkin
```

  </div>
</details>

<details>
  <summary>part 5 / Prometheus and Grafana</summary>
  <div markdown="1">
  
[Prometheus](https://prometheus.io/download/)에서는 직접 도커 이미지를 제공하고 있기에 해당 주소로 들어가 사용하는 것이 좋다.
  
[Grafana](https://grafana.com/grafana/download?pg=get&plcmt=selfmanaged-box1-cta1) 또한 제공을 하고 있기 때문에 `Prometheus`, `Grafana`를 띄울 때 네트워크 설정만 주의하여 띄우면 될 것이다.

추가적으로 `Prometheus`는 기존에 사용했던 `Prometheus.yml`을 사용할 예정이기 때문에

```yml
-v /path/to/prometheus.yml:/etc/prometheus/prometheus.yml
```

설정을 추가, `Grafana`의 경우는 네트워크 설정만 주의하면 된다.

`Prometheus.yml`파일 내부에는 로컬호스트와 포트번호를 이용하여 엔트포인트를 지정 했었기 때문에 해당 부분을 수정을 해야 한다.

```bash
$ docker run -d -p 9090:9090 \
          --network ecommerce-network \
          --name prometheus \
          -v /d/prometheus/prometheus-2.54.1.windows-amd64/prometheus.yml:/etc/prometheus/prometheus.yml \
          prom/prometheus
```

```bash
$ docker run -d -p 3000:3000 \
          --network ecommerce-network \
          --name grafana \
          grafana/grafana
```

  </div>
</details>

- 현재 시점에서 도커 `ecommerce-network` 네트워크 현황

| 서비스            | IP              | Port | 서비스                | IP              | Port |
| ----------------- | --------------- | ---- | --------------------- | --------------- | ---- |
| Rabbitmq          | 172.19.0.2/16   | 5671 | Configuration-Service | 172.19.0.3/16   | 8888 |
| Discovery-Service | 172.19.0.4/16   | 8761 | ApiGateway-Service    | 172.19.0.5/16   | 8000 |
| MariaDB           | 172.19.0.6/16   | 3306 | Zipkin                | 172.19.0.7/16   | 9411 |
| Prometheus        | 172.19.0.8/16   | 9090 | Grafana               | 172.19.0.9/16   | 3000 |
| Zookeeper         | 172.19.0.100/16 | 2181 | Kafka                 | 172.19.0.101/16 | 9092 |

<details>
  <summary>part 5 / User Microservice, Order Service, Catalog Service</summary>
  <div markdown="1">

## User Microservice

해당 프로젝트 내 yml 파일 내부 `Zipkin`과 `RabbitMQ`등의 주소값을 직접 수정하지 않고 나중에 컨테이너를 띄울 때 명시해줄 것이다.

그런데 `bootstrap.yml`내용을 `Configuration-Service`에서 값을 갖고 오기 때문에 해당 값을 수정해줘야 한다.

[Configuration-Service user-service(default)](http://127.0.0.1:8888/user-service/default)

```bash
$ docker run -d --network ecommerce-network \
          --name user-service \
          -e "spring.cloud.config.uri=http://config-service:8888" \
          -e "spring.rabbitmq.host=rabbitmq" \
          -e "spring.zipkin.base-url=http://zipkin:9411" \
          -e "management.zipkin.tracing.endpoint=http://zipkin:9411/api/v2/spans"  \
          -e "eureka.client.serviceUrl.defaultZone=http://discovery-service:8761/eureka/" \
          -e "logging.file=/api-logs/users-ws.log" \
          jae9380/user-service:1.0
```

## Order Microservice

```bash
$ docker run -d --network ecommerce-network --name order-service \
          -e "spring.zipkin.base-url=http://zipkin:9411" \
          -e "management.zipkin.tracing.endpoint=http://zipkin:9411/api/v2/spans"  \
          -e "eureka.client.serviceUrl.defaultZone=http://discovery-service:8761/eureka/" \
          -e "spring.datasource.url=jdbc:mariadb://mariadb:3306/mydb" \
          -e "logging.file=/api-logs/orders-ws.log" \
          jae9380/order-service
```

## Catalog Microservice

```bash
$ docker run -d --network ecommerce-network \
        --name catalog-service \
        -e "eureka.client.serviceUrl.defaultZone=http://discovery-service:8761/eureka/" \
        -e "logging.file=/api-logs/catalogs-ws.log" \
        jae9380/catalog-service:1.0
```

  </div>
</details>

<details>
  <summary>part 6 / Total Test</summary>
  <div markdown="1">
  
모든 프로젝트를 컨테이너로 띄웠으니 잘 동작하는지 테스트를 진행

- `User-Microservice` 403  
   `health_check` 테스트 시 403에러가 발생한다.  
   해당 문제는 `WebSecurity.java`파일에서 `hasIpAddress()`에 설정된 IP가 일치하지 않기에 발생하는 문제이다.

  기존에 띄우 컨테이너를 지우고 기존의 파일에서 수정하여 다시 띄우면 간단하게 해결이 가능

    </div>
  </details>

_토글_

```html
<details>
  <summary>part</summary>
  <div markdown="1"></div>
</details>
```
