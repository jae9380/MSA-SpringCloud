
# Section 1

## part 1
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