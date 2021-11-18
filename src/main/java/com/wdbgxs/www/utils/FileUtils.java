package com.wdbgxs.www.utils;

import java.io.File;

/**
 * @description 文件处理工具类
 * @auther zhangshuai
 * @date 2021/11/17 15:31
 */
public class FileUtils {

    public static String dealFilePathPrefix(String prefix,File file){
        String absolutePath = file.getPath();
        absolutePath = absolutePath.replace(prefix,"").replaceAll("\\\\","\\.").replace(".class","");
        return absolutePath;
    }





}
