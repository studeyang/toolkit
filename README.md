# 概览

- toolkit-commons：原自 icec-cloud-commons
- toolkit-config：配置中心基础包
- toolkit-spring：集成 spring 的基础包
- toolkit-web：原自 icec-cloud-base-webapp

# toolkit-spring

## 功能一：打印请求日志

添加依赖

```xml
<dependency>
    <groupId>io.github.studeyang</groupId>
    <artifactId>toolkit-commons</artifactId>
</dependency>
```

开启功能

```java
@SpringBootApplication
@EnableRequestLog
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }

}
```

接口配置

```java
@RestController
public class ExampleController {
    @PostMapping("/example/user")
    @RequestLog
    public Response requestLogPost(@RequestBody Request user) {
        Response response = new Response();
        response.setCode(200);
        response.setMessage(user.getName() + " create success");
        return response;
    }
}
```

```java
@Data
public class Request {
    private String name;
    private Integer age;
}

@Data
public class Response {
    private Integer code;
    private String message;
}
```

打印效果

```verilog
POST /example/user, params: {}, body: {"name":"studeyang","age":25}
response body: {"code":200,"message":"studeyang create success"}
```

