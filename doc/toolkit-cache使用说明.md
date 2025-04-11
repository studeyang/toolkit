# toolkit-cache

原自 fcbox-cache，不同点：getAllCacheStats 兼容展示了发布订阅 channel

## 功能一：管理本地缓存

### 添加依赖

```xml
<dependency>
    <groupId>io.github.studeyang</groupId>
    <artifactId>toolkit-spring-boot-starter</artifactId>
</dependency>
<dependency>
    <groupId>io.github.studeyang</groupId>
    <artifactId>toolkit-cache</artifactId>
</dependency>
```

### 开启功能

```java
@SpringBootApplication
@EnableCache
public class WebApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}
```

```yaml
################################################
# application.yml 配置Redis以便刷新所有节点的缓存
################################################
spring:
  redis:
    client-name: example
    cluster.nodes: ${redis.nodes}
    password: ${redis.password}
    cluster.max-redirects: 3
    jedis.pool.maxIdle: 50
    jedis.pool.maxActive: 50
    jedis.pool.minIdle: 10
    jedis.pool.maxWait: 3000
    timeout: 3000
```

### 实现缓存类

```java
public class UserCacheImpl extends AbstractLoadingCache<String, UserEntity> {

    public UserCacheImpl() {
        // 最大缓存条数
        setMaximumSize(5);
        setTimeUnit(TimeUnit.DAYS);
        setExpireAfterWriteDuration(37);
    }

    @Override
    public UserEntity get(String key) {
        try {
            return getValue(key);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public UserEntity loadData(String key) {
        // 模拟从数据库读取
        UserEntity user = new UserEntity();
        user.setId(key);
        user.setUserName("人员" + key);
        return user;
    }
}
```

```java
// 加载缓存
@Component
public class CacheLoader implements ApplicationRunner {

    @Autowired
    private UserCacheImpl userCacheImpl;

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("userCacheImpl: " + userCacheImpl.get("01"));
        System.out.println("userCacheImpl: " + userCacheImpl.get("02"));
    }
}
```

### 查看缓存

访问：http://localhost:8080/cache/getAllCacheStats

![image-20250411171536268](https://technotes.oss-cn-shenzhen.aliyuncs.com/2024/202504111715683.png)

### 刷新所有节点缓存

```java
@RestController
public class ExampleController {
    @Autowired
    private IGuavaCachePublisher guavaCachePublisher;
    
    @GetMapping("/example/cache")
    public String refreshCache(@RequestParam String cacheName, @RequestParam String cacheKey) {
        guavaCachePublisher.publish(cacheName, cacheKey);
        return "success";
    }
}
```

```shell
curl --location --request GET 'http://localhost:8080/example/cache?cacheName=sendDictionaryCacheService&cacheKey=01' \
--header 'Accept: */*' \
--header 'Host: localhost:8080' \
--header 'Connection: keep-alive'
```

