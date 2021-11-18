package com.wdbgxs.www.annotationParse;

import com.wdbgxs.www.annotation.Resource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @description Resource注解的解析类
 * @auther zhangshuai
 * @date 2021/11/18 15:00
 */
public class ResourceParse implements AnnotationParse{


    @Override
    public String parse(Class clazz) {
        Resource resource = (Resource) clazz.getAnnotation(Resource.class);
        if (resource.value().equals("")||resource.value() == null){
            //这个时候返回字段的名称
            String fieldName = "";
            try {
                Field field = (Field) clazz.newInstance();
                field.setAccessible(true);
                fieldName = field.getName();
            } catch (InstantiationException e) {
                //实例化失败的原因有很多，包括但不限于:类对象表示抽象类、接口、数组类、基本类型或void,该类没有空构造函数
                e.printStackTrace();
            } finally {
                return fieldName;
            }
        }else {
            return resource.value();
        }
    }
}
