package com.wdbgxs.www.beanFactory;


public interface ConfigureableBeanFactory {

    //用于在初始化Bean工厂前设置一些信息，暂时不知道需不需要参数
    public void beforeInitBeanFactory();

    //初始化Bean工厂，进行包扫描
    public void initBeanFactory(String pathOrXmlName);

    //用于在初始化Bean工厂完成之后 执行的一些事情，暂时也不知道需不需要参数
    public void afterInitBeanFactory();

    //刷线Bean工厂
    void refresh();

    //根据实体对象的名称，从Bean工厂中获取到bean实例
    <T> T getBean(String beanName);

    //根据实体类的类型获取其实例对象
    <T> T getBean(Class classType);
}
