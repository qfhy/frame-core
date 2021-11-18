package com.wdbgxs.www.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description 扫描的实例化对象路径设置注解
 * @auther zhangshuai
 * @date 2021/11/18 16:16
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ComponentScan {

    String value() default "";

}
