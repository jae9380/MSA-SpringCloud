# Section 3

## Utilize Kafka for data synchronization

- Apache Kafka 이전
  관계형 데이터 베이스나 NoSQL 데이터베이스, 어플리케이션, 기타의 스토리지 서버가 있다고 가정을 하자

다양한 리소스로부터 데이터를 받아서 특정한 서비스 또는 특정한 시승템에 데이터를 보관을 하고 다양한 형태의 서비스에 데이터를 전달하는 등 이렇게 `end-to-end`방식으로 연결되어 아키텍처에서 데이터의 연동이 복잡하게 될 수밖에 없다. 이러면 장애에 대해서 굉장히 민감하게 반응을 한다. 그리고 서로 다른 파이프 연결 구조를 갖고 있어야 되며, 모니터링 시스템에서도 서로 다른 포멧을 사용하고 있기에 유지 및 보수 부분에서도 번거롭다.

이렇기 때문에 모든 시스템으로 데이터를 실시간으로 전송하여 처리할 수 있는 시스템, 데이터가 많아지더라도 확장이 용이한 시스템의 필요성이 대두되었다.

<details>
  <summary>part 1 / Producer and Consumer</summary>
  <div markdown="1">

[Kafka-docker](https://github.com/wurstmeister/kafka-docker) 프젝트를 `clone`하여 내부에 있는 `docker-compose-single-broker.yml`의 내용을 아래와 강티 수정을 하고 실행
```yaml
version: '2'
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

```shell
docker compose -f docker-compose-single-broker.yml up -d
```

```shell
# List
docker exec kafka-docker-kafka-1 /opt/kafka_2.13-2.8.1/bin/kafka-topics.sh --bootstrap-server localhost:9092 --list

# Create
docker exec kafka-docker-kafka-1 /opt/kafka_2.13-2.8.1/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic quickstart-events --partitions 1

# Describe
docker exec kafka-docker-kafka-1 /opt/kafka_2.13-2.8.1/bin/kafka-topics.sh --bootstrap-server localhost:9092 --describe quickstart-events

# Producer
docker exec -it kafka-docker-kafka-1 /opt/kafka_2.13-2.8.1/bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic quickstart-events

# Consumer
docker exec -it kafka-docker-kafka-1 /opt/kafka_2.13-2.8.1/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic quickstart-events --from-beginning
```

---
### 간단한 개념 정리    
1.	토픽(Topic):
	*	정의: 메시지를 구분하기 위한 카테고리입니다. 각 토픽은 독립적인 메시지 스트림을 제공합니다.
	*	예시: 예를 들어, user-logs, order-events, sensor-data와 같은 토픽을 설정하여 각기 다른 유형의 메시지를 구분할 수 있습니다.

2.	파티션(Partition):
	*	정의: 각 토픽은 여러 개의 파티션으로 나뉩니다. 파티션은 메시지를 저장하는 물리적 단위입니다.
	*	용도: 파티션을 사용하여 데이터의 병렬 처리를 가능하게 하고, 데이터의 스케일을 조절할 수 있습니다.

3.	복제(Replication):
	*	정의: 각 파티션은 복제본을 가집니다. 복제는 데이터의 내구성과 고가용성을 보장합니다.
	*	용도: 복제본은 여러 브로커에 걸쳐 분산되어 있어, 브로커 장애 시에도 데이터 손실을 방지할 수 있습니다.

4.	프로듀서(Producer):
	*	정의: 데이터를 Kafka에 게시하는 클라이언트입니다.
	*	용도: 프로듀서는 특정 토픽에 메시지를 전송합니다.

5.	컨슈머(Consumer):
	*	정의: Kafka에서 데이터를 읽어오는 클라이언트입니다.
	*	용도: 컨슈머는 특정 토픽에서 메시지를 읽어오고 처리합니다.

6.	컨슈머 그룹(Consumer Group):
	*	정의: 여러 컨슈머가 함께 작업하여 하나의 토픽에서 메시지를 처리하는 그룹입니다.
	*	용도: 컨슈머 그룹을 사용하면 메시지를 병렬로 처리할 수 있으며, 메시지 처리의 부하를 분산시킬 수 있습니다.

* kafka-topics.sh 스크립트를 사용하여 토픽을 관리 명령어
	*	--list: 모든 토픽의 목록을 조회합니다. 클러스터에 어떤 토픽들이 있는지 알고 싶을 때 사용합니다.
	*	--create: 새 토픽을 생성합니다. 새로운 토픽을 클러스터에 추가할 때 사용합니다.
	*	--describe: 특정 토픽의 세부 정보를 조회합니다. 토픽의 설정과 상태를 확인하고 싶을 때 사용합니다.

---

  </div>
</details>

_토글_

```html
<details>
  <summary>part</summary>
  <div markdown="1"></div>
</details>
```
