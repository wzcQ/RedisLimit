package com.example.demo.config;

import com.example.demo.tool.IpUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


/**
 * 拦截器
 *
 */
@Component
public class RequestLimitInterceptor implements HandlerInterceptor {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            if (handler instanceof HandlerMethod) {
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                // 获取 方法注解 RequestLimit
                RequestLimit requestLimit = handlerMethod.getMethodAnnotation(RequestLimit.class);
                if (null == requestLimit) {
                    return true;
                }
                // 时间
                int seconds = requestLimit.second();
                // 最大次数
                int maxCount = requestLimit.count();
                String ipAddr = IpUtils.getIpAddress(request);
                // 存储key
                String key = ipAddr + ":" + request.getContextPath() + ":" + request.getServletPath();
                // 已经访问的次数 缓存获取
                Integer count = (Integer) redisTemplate.opsForValue().get(key);
                log.info("检测到目前ip对接口={}已经访问的次数", request.getServletPath(), count);
                // 为空则放入缓存  并设置次数1,和缓存有效时间
                if (null == count || -1 == count) {
                    redisTemplate.opsForValue().set(key, 1, seconds, TimeUnit.SECONDS);
                    return true;
                }
                // 比较 当前访问次数
                if (count < maxCount) {
                    redisTemplate.opsForValue().increment(key);
                    return true;
                }
                log.warn("请求过于频繁请稍后再试");
                returnData(response);
                return false;
            }
            return true;
        } catch (Exception e) {
            log.warn("请求过于频繁请稍后再试");
            e.printStackTrace();
        }
        return true;
    }

    private void returnData(HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        ObjectMapper objectMapper = new ObjectMapper();
        //这里传提示语可以改成自己项目的返回数据封装的类
        response.getWriter().println(objectMapper.writeValueAsString("请求过于频繁请稍后再试"));
        return;
    }
}
