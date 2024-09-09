# Section 3

## Utilize Kafka for data synchronization

- Apache Kafka 이전
  관계형 데이터 베이스나 NoSQL 데이터베이스, 어플리케이션, 기타의 스토리지 서버가 있다고 가정을 하자

다양한 리소스로부터 데이터를 받아서 특정한 서비스 또는 특정한 시승템에 데이터를 보관을 하고 다양한 형태의 서비스에 데이터를 전달하는 등 이렇게 `end-to-end`방식으로 연결되어 아키텍처에서 데이터의 연동이 복잡하게 될 수밖에 없다. 이러면 장애에 대해서 굉장히 민감하게 반응을 한다. 그리고 서로 다른 파이프 연결 구조를 갖고 있어야 되며, 모니터링 시스템에서도 서로 다른 포멧을 사용하고 있기에 유지 및 보수 부분에서도 번거롭다.

이렇기 때문에 모든 시스템으로 데이터를 실시간으로 전송하여 처리할 수 있는 시스템, 데이터가 많아지더라도 확장이 용이한 시스템의 필요성이 대두되었다.

<details>
  <summary>part 1 / </summary>
  <div markdown="1">

```shell
docker run --name my_kafka_container --env CONFIG_NAME=CONFIG_VALUE -p 9092:9092 apache/kafka:3.8.0
```

```xml
<!-- https://mvnrepository.com/artifact/org.apache.kafka/kafka-clients -->
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-clients</artifactId>
    <version>3.8.0</version>
</dependency>
```

`Kafka`자체를 관리하는 `Zookeeper`가 필요하다. 그렇기 때문에 `Zookeeper`를 먼저 기동을 하고 다음으로 `Kafka`를 기동한다.

카프카는 기본적으로 프로듀서에서 메세지를 보내게 되면 해당 데이터는 `Topic`이라는 곳에 저장을 한다. 해당 `Topic`은 임의로 자유롭게 생성할 수 있다.

  </div>
</details>

_토글_

```html
<details>
  <summary>part</summary>
  <div markdown="1"></div>
</details>
```
