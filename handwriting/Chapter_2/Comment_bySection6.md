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

_토글_

```html
<details>
  <summary>part</summary>
  <div markdown="1"></div>
</details>
```
