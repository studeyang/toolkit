# toolkit-config

## 功能：配置预加载

### 添加依赖

```xml
<dependency>
    <groupId>io.github.studeyang</groupId>
    <artifactId>toolkit-config</artifactId>
</dependency>
```

### 开启功能

```java
@SpringBootApplication
@PrepareConfigurations(group = "commons", value = {"test.yml"})
public class WebApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}
```

```yaml
####################################
# application.yml
####################################
nacos:
  config:
    server-addr: ${nacos.server-addr}
    namespace: ${nacos.namespace}
    username: ${nacos.username}
    password: ${nacos.password}
```

### 运行结果

目前支持的配置中心有：SpringCloudConfig，Nacos

```yaml
####################################
# Nacos配置
# Data ID: test.yml
# Group: commons
####################################
test:
  config: nacos
```

```java
@Component
public class ToolkitConfigExampleRunner implements ApplicationRunner {

    @Value("${test.config}")
    private String config;

    @Autowired
    private Environment environment;

    @Override
    public void run(ApplicationArguments args) {
        System.out.println(config);
    }
}
```

通过 environment，可以看到预加载配置的排序。（Spring的配置读取机制是越在前，越优先）

![image-20250411151551705](https://technotes.oss-cn-shenzhen.aliyuncs.com/2024/202504111515900.png)

