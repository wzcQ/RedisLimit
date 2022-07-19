package com.example.demo.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截 配置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private RequestLimitInterceptor requestLimitInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        /**
         * addPathPatterns  拦截添加所有接口
         *
         * excludePathPatterns 筛选 static开头的接口 和login登录接口
         */
        registry.addInterceptor(requestLimitInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/static/**","/auth/login");
    }
}
