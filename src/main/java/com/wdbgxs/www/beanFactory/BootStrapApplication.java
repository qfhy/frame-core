package com.wdbgxs.www.beanFactory;

import com.wdbgxs.www.annotationParse.ComponentScanParse;

import java.net.URL;

/**
 * @description 项目环境初始化和启动类
 * @auther zhangshuai
 * @date 2021/11/18 15:43
 */
public class BootStrapApplication {

    public static void run(Class<?> mainClass){
        URL resource = mainClass.getResource("/");
        AnnotationBeanFactory instance = AnnotationBeanFactory.getInstance();
        instance.setProjectClassPath(resource.getFile().replace("/","\\").substring(1));
        instance.initBeanFactory(new ComponentScanParse().parse(mainClass));
        System.out.println("初始化完成。。。。。。。");
    }

}
