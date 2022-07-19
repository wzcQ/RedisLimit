package com.example.demo.controller;

import com.example.demo.config.RequestLimit;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
public class RedisController {

    /**
     * 测试  60秒  3次
     * @return
     */
    @GetMapping("/test")
    @RequestLimit(second = 60,count = 3)
    public @ResponseBody String get() {
        return ""+ new Date();
    }

    /**
     * 登录 接口 不会被拦截限制
     * @param name
     * @param Password
     * @return
     */
    @GetMapping("auth/login")
    @RequestLimit(second = 60,count = 3)
    public @ResponseBody Boolean login(String name,String Password) {
        return true;
    }

    /**
     * static 开头方法
     * @return
     */
    @GetMapping("static/login")
    @RequestLimit(second = 60,count = 3)
    public @ResponseBody String staticMethod() {
        return "staticMethod";
    }
}
