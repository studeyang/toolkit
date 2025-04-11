package io.github.toolkit.example.controller;

import io.github.toolkit.commons.annotation.RequestLog;
import io.github.toolkit.cache.pubsub.IGuavaCachePublisher;
import io.github.toolkit.example.dto.Request;
import io.github.toolkit.example.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2023/5/9
 */
@RestController
public class ExampleController {

    @Autowired
    private IGuavaCachePublisher guavaCachePublisher;

    @GetMapping("/example/noRequestLog/name")
    public String noRequestLogGet(@RequestParam Integer id) {
        return "name-" + id;
    }

    @PostMapping("/example/noRequestLog/user")
    public Response noRequestLogPost(@RequestParam String remark, @RequestBody Request user) {
        Response response = new Response();
        response.setCode(200);
        response.setMessage(user.getName() + " create success, remark: " + remark);
        return response;
    }

    // ========== request log ===========

    @GetMapping("/example/name")
    @RequestLog
    public String requestLogGet(@RequestParam Integer id) {
        return "name-" + id;
    }

    @PostMapping("/example/user")
    @RequestLog(responseLog = true)
    public Response requestLogPost(@RequestBody Request user) {
        Response response = new Response();
        response.setCode(200);
        response.setMessage(user.getName() + " create success");
        return response;
    }

    @PostMapping("/example/ping")
    @RequestLog
    public void ping() {

    }

    // ========== cache ===========

    @GetMapping("/example/cache")
    public String refreshCache(@RequestParam String cacheName, @RequestParam String cacheKey) {
        guavaCachePublisher.publish(cacheName, cacheKey);
        return "success";
    }

}
