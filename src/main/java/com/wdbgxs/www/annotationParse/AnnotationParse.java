package com.wdbgxs.www.annotationParse;

/**
 * @description 注解解析的通用接口父类
 * @auther zhangshuai
 * @date 2021/11/18 14:04
 */
public interface AnnotationParse {

    public <T> T parse(Class clazz);

}
