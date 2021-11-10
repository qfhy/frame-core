package com.wdbgxs.www.beanFactory;

import com.wdbgxs.www.exception.CustomTypeException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 该类为注解解析的bean工厂类
 * @author qfhy
 * @date 2021-11-09-23:23:00
 */
public class AnnotationBeanFactory implements ConfigureableBeanFactory{

    //创建一个集合，用于保存反射创建的对象
    private static final Map<Class<?>,Object> mapClass = new HashMap<>();

    //用于保存生成的实例对象，根据设置的对象名称进行保存
    private static final Map<String,Object> mapNameClassObj = new HashMap<>();

    //静态保存注解Bean工厂的对象实例
    private static final AnnotationBeanFactory annotationBeanFactory = new AnnotationBeanFactory();

    //私有化注解Bean工厂，实现简答的单实例化
    private AnnotationBeanFactory(){
    }

    //创建一个方法，能够让外接获取到单实例Bean工厂对象
    public AnnotationBeanFactory getInstance(){
        return annotationBeanFactory;
    }

    @Override
    public void beforeInitBeanFactory() {

    }

    @Override
    public void initBeanFactory(String pathOrXmlName) {

    }


    //获取所有的Bean,解析指定的包下的所有文件，获取类文件地址
    private void getAllBeans(String path){


    }


    //处理需要扫描的包路径地址格式
    private void checkPackagePath(String path) throws CustomTypeException {
        if (path == null || path.equals("")){
            throw new CustomTypeException(1,"");
        }
    }



    //首先解析
    private void recursionBeanPath(String path, List<String> javaFileList){

    }




    @Override
    public void afterInitBeanFactory() {

    }

    @Override
    public void refresh() {

    }

    @Override
    public <T> T getBean(String beanName) {
        return null;
    }

    @Override
    public <T> T getBean(Class classType) {
        return null;
    }



}
