package com.wdbgxs.www.annotationParse;

import com.wdbgxs.www.annotation.ComponentScan;

/**
 * @description 实体扫描路径注解解析类
 * @auther zhangshuai
 * @date 2021/11/18 16:30
 */
public class ComponentScanParse implements AnnotationParse{

    @Override
    public String parse(Class clazz) {
        ComponentScan componentScan = (ComponentScan)clazz.getAnnotation(ComponentScan.class);
        if (componentScan.value().equals("")||componentScan.value() == null){
            return "";
        }else {
            return componentScan.value();
        }
    }
}
