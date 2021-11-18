package com.wdbgxs.www.annotationParse;

import com.wdbgxs.www.annotation.Component;

import java.lang.annotation.Annotation;

/**
 * @description Component注解的解析类
 * @auther zhangshuai
 * @date 2021/11/18 14:02
 */
public class ComponentParse implements AnnotationParse{

    @Override
    public String parse(Class clazz) {
        Component component = (Component) clazz.getAnnotation(Component.class);
        if (component.value().equals("")||component.value() == null){
            return clazz.getName();
        }else {
            return component.value();
        }
    }

}
