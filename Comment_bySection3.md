# Section 3


<details>
  <summary>part 1 / Welcome Method</summary>
  <div markdown="1">
    간단하게 String을 반환해주는 GET 매핑을 만들었다.    
  문자열을 반환해주는 방법은 3가지로 모든 방법을 연습으로 작성했다.   

  ![](https://i.postimg.cc/RFK4FMRb/welcome.png)   
  간단하게 바로 문자열을 반환하는 경우   

  다음으로, yml로 가서 직접 메세지를 작성하고 해당 메세지를 갖고 반환하는 방법    
  ```yaml
  greeting:
    message: Welcome to the Simple E-commerce.
  ```
  그리고 컨트롤러 클래스에서
  ```java
  private Environment env;

  ...

      @GetMapping("welcome")
      public String welcome() {
        return env.getProperty("greeting.message");
      }
  ```   
  `Environment`객체를 이용하여 반환   

  마지막으로 클래스를 만들어 
  ![](https://i.postimg.cc/y88RSG2T/greeting-Class.png)

  ```java
    @Autowired
    private Greeting  greeting;

    ...
//        return env.getProperty("greeting.message");
        return greeting.getMessage();
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
