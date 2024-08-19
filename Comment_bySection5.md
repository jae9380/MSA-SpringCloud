# Section 5

## Users Microservice

섹션4 단계에서 작업한 Users Microservice에 로그인 관련 기능을 추가할 계획

- JWT (Json Web Token)
- API Gateway service - AuthorizationHeader Filter

<details>
  <summary>part 1 / </summary>
  <div markdown="1">   
로그인 시 사용자가 전달한 값을 저장하기 위한 클래스로 RequestLogin 생성

- UsernamePasswordAuthenticationFilter
  Spring Security에서 사용자의 아이디와 비밀번호 기반으로 인증을 처리하는 필터이다. 이는 기본적으로 로그인을 처리하며, 사용자 정보를 검증하고 인증토큰을 생성하는 역활을 한다.  
   그렇기 때문에 두 메소드를 재정의하여 기본 인증과정을 커스터마이징을 한다.

  - attemptAuthentication  
    로그인 요청이 들어올 때, 사용자의 자격을 기반으로 인증을 시도하는 역활을 한다. 사용자로부터 받은 `HttpServletRequest` 객체에서 아이디와 비밀번호를 추출하고 이를 기반으로 `Authentication`객체를 생성한다. 그런 다음 생성된 객체를 `AuthenticationManager`에게 전달하여 실제 인증을 수행하게 한다.

    `attemptAuthentication`을 재정의하면, 기본 인증 과정에서 커스터마이징이 필요한 부분을 추가할 수 있다. 예를 들어 요청 파라미터에서 아이디와 비밀번호를 추출하는 방법을 커스터마이징을 하거나, 추가적인 검증 로직을 넣을 수 있다.

  - SuccessfulAuthentication  
    해당 메소드는 사용자가 성공적으로 인증이 되었을 때 호출된다. 이 메소드는 보통 인증 성공 후 추가 작업을 처리하는데 사용이 된다. 예를 들어 JWT 토큰을 발급하거나 사용자의 인증 정보를 세션에 저장하는 등의 작업을 수행할 수 있다.

    `successfulAuthentication` 메소드를 재정의를 하면, 인증 성공 후의 처리를 커스터마이징을 하는데 예를 들어서 JWT 기반의 인증을 하는 경우, 이 메소드에서 JWT토큰을 생성하여 헤더에 추가할 수 있다.

* JSON 요청 데이터 파싱

```java
  RequestLogin creds = new ObjectMapper().readValue(request.getInputStream(), RequestLogin.class);
```

이 부분에서는 클라이언트가 보낸 HTTP 요청의 바디를 읽어와 RequestLogin이라는 클래스 형태로 변환한다. ObjectMapper는 Jackson 라이브러리에서 제공하는 클래스이며, JSON 데이터를 Java 객체로 변환하는 데 사용

request.getInputStream()을 통해 요청 바디를 스트림으로 가져온다.  
이 스트림을 Jackson의 readValue 메서드를 사용하여 RequestLogin 객체로 변환한다. 이 RequestLogin 클래스는 보통 로그인 시 사용되는 email과 password 필드를 가지고 있다.

- 인증 토큰 생성 및 인증 시도

```java
return getAuthenticationManager().authenticate(
        new UsernamePasswordAuthenticationToken(
                creds.getEmail(),
                creds.getPassword(),
                new ArrayList<>())
);
```

UsernamePasswordAuthenticationToken 객체를 생성하여, 사용자가 입력한 이메일과 비밀번호를 포함한 인증 토큰을 만든다.  
이 인증 토큰은 AuthenticationManager에 전달되어 실제 인증을 수행

- 예외 처리

```java
} catch (IOException e) {
    throw new RuntimeException(e);
}
```

JSON 파싱 과정에서 발생할 수 있는 IOException을 처리하기 위해 예외 처리를 한다.  
만약 JSON 파싱에 실패하면, RuntimeException을 발생시킵니다.

  </div>
</details>

_토글_

```html
<details>
  <summary>part</summary>
  <div markdown="1"></div>
</details>
```
