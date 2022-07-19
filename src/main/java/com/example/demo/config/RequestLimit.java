package com.example.demo.config;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestLimit {

    /**
     * 时间范围  默认1秒
     * @return
     */
    int second() default 1;

    /**
     * 最大次数  默认1000次
     * @return
     */
    int count() default 1000;
}
