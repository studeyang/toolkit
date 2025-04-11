package io.github.open.toolkit.example.controller;

import io.github.open.toolkit.annotation.RequestLog;
import io.github.open.toolkit.example.dto.Request;
import io.github.open.toolkit.example.dto.Response;
import org.springframework.web.bind.annotation.*;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2023/5/9
 */
@RestController
public class ExampleController {

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
    @RequestLog(responseLog = false)
    public String requestLogGet(@RequestParam Integer id) {
        return "name-" + id;
    }

    @PostMapping("/example/user")
    @RequestLog
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

}
