# Section 1

## Encryption of configuration data

### symmetric key and asymmetric key

암호화 처리에 있어 두 가지 방법에 대하여 살펴 볼 것이다.

일반적인 데이터, 플랜 텍스트, 평문으로 되어있는 것을 암호화 하는 것이 Encryption이고, 반대로 원래 상태로 복구하는 것이 decryption 이다.

여기서 암호화와 복호화에 있어 같은 키를 사용하는 경우를 **대칭 암호화** 방식 `Semantic Encryption`방식이라고 한다.  
 `Asymmetric Encryption`이라고 해서 암호화와 복호화에 사용되는 키가 다른 **비대칭 암호화** 방식 이라고 한다.

비대칭 암호화 방식인 `Asymmetric Encryption`에서 사용되는 각각의 키를 Public, Private으로 사용하는 방식과 자바에서 이러한 각각 공개 범위가 다른 키를 생성하기 위한 Java JDK 내부에 포함된 keytool이라는 프로그램을 사용하여 키값을 생성할 수 있다.

![](https://i.postimg.cc/8cC8DWfh/encryption.png)

<details>
  <summary>part 1 / Practice encryption using symmetric keys.</summary>
  <div markdown="1">
  
  ```yaml
  encrypt:
    key: abcdabcdabcd
  ```
  이와 같이 설정을 하면 해당 값은 대칭키로 사용이 된다.   
  이렇게 configuration서버에 `bootstrap.yml`파일에 작성을 하면 암호화 작업은 끝났다.   

  이후 POST 방식으로 `127.0.0.1:8888/encrypt`로 body에 아무런 값이나 작성을 하면 해당 값이 암호화를 거쳐서 나타나게 된다.    

  그리고 암호화를 거친 값을 갖고 `127.0.0.1:8888/decrypt`에 값을 보내면 암호화 이전의 값을 다시 반환을 해주는 복호화 작업을 해준다.   

User-Microservice에서 유저에 대한 데이터를 추적하는 설정코드를 암호화 하기 위해서 Configuration서버로 옮겨서 작성을 한다. 추가적으로 데이터 베이스의 password는 암호화 하여 작성을 한다.   
여기서 암호화를 거친 값을 그냥 붙여 쓰는것이 아닌 해당 값은 암호화를 더쳤다고 명시하기 위해서 싱글쿼터로 감싸고 `{cipher}`을 암호화 한 값 앞에 붙여준다.(싱글 쿼터 내부)   

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: '{cipher}494c0ed8763101776514e32113ff15b509c3005922f426ee88714490b9dc1fe7'
```

만약 암호화를 거친 값에 임의적으로 아무런 값을 추가로 작성을 하면, 해당 값을 복호화 할 때 해당 값은 `invalid`값이라고 나타난다.   

  </div>
</details>

_토글_

```html
<details>
  <summary>part</summary>
  <div markdown="1"></div>
</details>
```
