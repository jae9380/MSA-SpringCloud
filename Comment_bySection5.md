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

<details>
  <summary>part4 / JWT Token</summary>
  <div markdown="1">

전통적인 인증 시스템  
클라이언트와 서버 사이 인증을 할 때, userName 과 password를 전달을 하여 데이터베이스에서 검사를 하여 세션을 발부하고, 그 세션을 이용하여 부가적인 동작을 진행을 하였다.

이러한 세션, 쿠키 방법은 모바일 어플리케이션에서 유효하게 사용할 수 없다는 점과 핸더링된 HTML페이지가 반환되지만, 모바일 어플리케이션에서는 JSON(or XML)과 같은 포멧이 필요하다.

그래서 나타난 방법이 Token 기반의 인증 시스템이다.  
일단 먼저 클라이언트가 사용자 한테 Authentication요청으로 userName과 password을 받아서 서버에서 인증 절차를 거친 후 세션을 발급하는 것이 아닌 토큰을 발급 한다.

[JWT](https://jwt.io/) Token 기반의 인증은 인증 헤더 내에서 사용되는 토큰 포멧으로 두 개의 시스템 사이에 안전한 방법으로 통신이 가능하다.

JWT 장점

- 독립적으로 클라이언트와의 통신이 가능한 방법이 생긴다.
- `CDN (Content Delivery Network)`라고 해서 중간 단계에서 캐시 서브를 놓을 수 있는데, 캐시 서버하고 인증 처리를 가능하게 해준다.
- 사이트 간 요청 정보 자체가 위변조가 될 가능성이 현저하게 떨어진다.
- 토큰 값을 다양한 형태로 저장하여 사용할 수 있다.

  </div>
</details>

_토글_

```html
<details>
  <summary>part</summary>
  <div markdown="1"></div>
</details>
```
