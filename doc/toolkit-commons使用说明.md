# toolkit-commons

## 功能一：打印请求日志

### 添加依赖

```xml
<dependency>
    <groupId>io.github.studeyang</groupId>
    <artifactId>toolkit-spring-boot-starter</artifactId>
</dependency>
<dependency>
    <groupId>io.github.studeyang</groupId>
    <artifactId>toolkit-commons</artifactId>
</dependency>
```

### 开启功能

```java
@SpringBootApplication
@EnableRequestLog
public class WebApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}
```

### 接口配置

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

### 打印效果

```shell
curl --location --request POST 'http://localhost:8080/example/user' \
--header 'Content-Type: application/json' \
--header 'Accept: */*' \
--header 'Host: localhost:8080' \
--header 'Connection: keep-alive' \
--data-raw '{
  "name": "demoData",
  "age": 1
}'
```

```java
Invoke io.github.toolkit.example.controller.ExampleController#requestLogPost, params: {"user":{"name":"demoData","age":1}}
Invoke io.github.toolkit.example.controller.ExampleController#requestLogPost, resp: {"code":200,"message":"demoData create success"}, cost：4
```

