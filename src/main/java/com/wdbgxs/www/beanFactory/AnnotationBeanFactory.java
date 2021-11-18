package com.wdbgxs.www.beanFactory;

import com.wdbgxs.www.annotation.Autowired;
import com.wdbgxs.www.annotation.Component;
import com.wdbgxs.www.annotation.Resource;
import com.wdbgxs.www.annotationParse.AnnotationParse;
import com.wdbgxs.www.exception.CustomExceptionInfoEnums;
import com.wdbgxs.www.exception.CustomTypeException;
import com.wdbgxs.www.utils.FileUtils;
import org.w3c.dom.ls.LSOutput;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 该类为注解解析的bean工厂类
 * @author qfhy
 * @date 2021-11-09-23:23:00
 */
public class AnnotationBeanFactory implements ConfigureableBeanFactory{

    //实际上也不应该放在这里
    private String projectClassPath = null;

    //java文件后缀的正则表达式
    private static final String JAVA_FILE_SUFIX_PATTERN = "^*.class$";

    //创建一个集合，用于保存反射创建的对象，设置为静态变量的原因是为了保证唯一，以及多线程安全，下面的几个集合也是一样
    private static final Map<Class<?>,Object> mapClass = new HashMap<>();

    //用于保存生成的实例对象，根据设置的对象名称进行保存
    private static final Map<String,Object> mapNameClassObj = new HashMap<>();

    //用于存储注解解析的实体对象
    private static final Map<Class,AnnotationParse> annotationParseMap = new HashMap<>();

    //静态保存注解Bean工厂的对象实例
    private static final AnnotationBeanFactory annotationBeanFactory = new AnnotationBeanFactory();

    //私有化注解Bean工厂，实现简答的单实例化
    private AnnotationBeanFactory(){
    }

    //创建一个方法，能够让外接获取到单实例Bean工厂对象
    public static AnnotationBeanFactory getInstance(){
        return annotationBeanFactory;
    }

    @Override
    public void beforeInitBeanFactory() {
        
    }

    @Override
    public void initBeanFactory(String pathOrXmlName) {
        try {
            //获取指定包路径下所有的类对象
            getAllBeans(pathOrXmlName);
            //实现对象的注入，暂时不针对于循环注入做处理，全都是非懒加载
            IOCAchieve();
        } catch (CustomTypeException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    //获取所有的Bean,解析指定的包下的所有文件，获取类文件地址
    private void getAllBeans(String path) throws CustomTypeException, ClassNotFoundException {
        path = checkPackagePath(path);
        List<String> pathList = new ArrayList<>();
        //递归遍历所有的路径找出所有符合的文件
        recursionBeanPath(new File(path),pathList);
        //进行类型创建
        initBeanForALLJavaFileList(pathList);
    }


    //处理需要扫描的包路径地址格式
    private String checkPackagePath(String path) throws CustomTypeException {
        //首先判定包路径是否为空
        if (path == null || path.equals("")){
            throw new CustomTypeException(CustomExceptionInfoEnums.PACKAGE_IS_NULL.getCode(),CustomExceptionInfoEnums.PACKAGE_IS_NULL.getMsg());
        }
        //处理包路径的格式,将路径中的点 . 分隔符，替换成当前运行的系统的默认的文件分隔符
        path = path.replaceAll("\\.","\\/");
        URL resource = this.getClass().getClassLoader().getResource(path);
        File file = new File(resource.getFile());
        if (!file.exists()){
            throw new CustomTypeException(CustomExceptionInfoEnums.PACKAGE_NOT_EXISTS.getCode(),CustomExceptionInfoEnums.PACKAGE_NOT_EXISTS.getMsg());
        }
        return file.getAbsolutePath();
    }


    //递归检验所有的路径，找出所有的JAVA文件
    private void recursionBeanPath(File file, List<String> javaFileList){
        if (file.isFile()){
            Pattern p = Pattern.compile(JAVA_FILE_SUFIX_PATTERN);
            Matcher m = p.matcher(file.getAbsolutePath());
            if (m.find()){
                javaFileList.add(FileUtils.dealFilePathPrefix(projectClassPath,file));
            }
            //终止本次递归
            return;
        }
        //文件对象的类型只有两种，不是文件，那么就是文件夹
        File[] files = file.listFiles();
        for (File item : files) {
            if (item.isFile()){
                Pattern p = Pattern.compile(JAVA_FILE_SUFIX_PATTERN);
                Matcher m = p.matcher(item.getAbsolutePath());
                if (m.find()){
                    javaFileList.add(FileUtils.dealFilePathPrefix(projectClassPath,item));
                }
            }else{
                recursionBeanPath(item,javaFileList);
            }
        }
    }

    //初始化所有的类路径
    private void initBeanForALLJavaFileList(List<String> javaFileList) throws ClassNotFoundException {
        for (String path : javaFileList){
            Class<?> clazz = Class.forName(path);
            Annotation[] annotations = clazz.getAnnotations();
            for (Annotation item : annotations) {
                if(item.annotationType() == Component.class){
                    //添加一到保险锁，放置处于多线程环境中进行对象创建
                    synchronized (item){
                        try {
                            Object obj = clazz.newInstance();
                            mapClass.put(clazz,obj);
                            //还要允许自定义实例名
                            String instanceName = getAnnoationParse(Component.class).parse(clazz);
                            mapNameClassObj.put(instanceName,obj);
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    //获取注解的解析实例对象
    private AnnotationParse getAnnoationParse(Class clazz){
        AnnotationParse parse = null;
        synchronized (clazz){
            parse = annotationParseMap.get(clazz);
            if (parse == null){
                //开始创建对应的解析类对象，又因为类型过于灵活，所以我们仍旧需要使用字符串拼接的方式，利用反射创建实例对象
                String classPath = "com.wdbgxs.www.annotationParse." + clazz.getSimpleName() + "Parse";
                try {
                    parse  = (AnnotationParse) this.getClass().getClassLoader().loadClass(classPath).newInstance();
                    //将创建好的实例对象放到缓存集合中
                    annotationParseMap.put(clazz,parse);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return parse;
    }

    //实现实例注入
    private void IOCAchieve() throws CustomTypeException {
        //遍历所有的对象
        for (Map.Entry<Class<?>,Object> entry : mapClass.entrySet()) {
            //检查所有的实例对象中的成员变量，检查其上有没有对应的注解
            Field[] fields = entry.getKey().getDeclaredFields();
            //成员变量遍历，找寻类型相同的数据
            for (Field item : fields) {
                //获取字段上的注解
                Autowired autowired = item.getAnnotation(Autowired.class);
                Resource resource = item.getAnnotation(Resource.class);
                if (autowired != null && resource != null){
                    throw new CustomTypeException(CustomExceptionInfoEnums.ANNOTATION_NOT_ALLOWED_COEXIST.getCode(),CustomExceptionInfoEnums.ANNOTATION_NOT_ALLOWED_COEXIST.getMsg());
                }
                //统一设置，防止私有变量不可操作
                item.setAccessible(true);
                if (autowired != null){
                    Class<?> type = item.getType();
                    Object obj = mapClass.get(type);
                    if (obj != null){
                        try {
                            item.set(entry.getValue(),obj);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }else {
                        throw new CustomTypeException(CustomExceptionInfoEnums.IOC_OBJECT_INJECTION_FAIL.getCode(),CustomExceptionInfoEnums.IOC_OBJECT_INJECTION_FAIL.getMsg());
                    }
                }else if (resource != null){
                    String instanceName = getAnnoationParse(Resource.class).parse(item.getClass());
                    //如果获取到的是一个空值那么就说明字段有问题
                    if (!instanceName.equals("")){
                        throw new CustomTypeException(CustomExceptionInfoEnums.IOC_OBJECT_INJECTION_FAIL.getCode(),CustomExceptionInfoEnums.IOC_OBJECT_INJECTION_FAIL.getMsg());
                    }
                    //根据对象实例名称查找对象实例
                    Object obj = mapNameClassObj.get(instanceName);
                    if (obj != null){
                        try {
                            item.set(entry.getValue(),obj);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }else{
                        //如果根据名称没有找到对象实例，那么根据类型查找
                        Class<?> type = item.getType();
                        Object objInstance = mapClass.get(type);
                        if (objInstance != null){
                            try {
                                item.set(entry.getValue(),objInstance);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }else {
                            throw new CustomTypeException(CustomExceptionInfoEnums.IOC_OBJECT_INJECTION_FAIL.getCode(),CustomExceptionInfoEnums.IOC_OBJECT_INJECTION_FAIL.getMsg());
                        }
                    }
                }
            }
        }

    }

    @Override
    public void afterInitBeanFactory() {

    }

    @Override
    public void refresh() {

    }

    //设置当前项目的ClassPath路径
    public void setProjectClassPath(String projectClassPath){
        this.projectClassPath = projectClassPath;
    }


    @Override
    public <T> T getBean(String beanName) {
        return (T) mapNameClassObj.get(beanName);
    }

    @Override
    public <T> T getBean(Class classType) {
        return (T) mapClass.get(classType);
    }



}
