package com.wdbgxs.www.exception;

/**
 * 该类为自定义抛出信息的异常类
 * @author qfhy
 * @date 2021-11-09 23:19:00
 */
public class CustomTypeException extends Exception{

    //错误码
    private int code;

    //错误信息
    private String errMsg;

    public CustomTypeException(int code,String errMsg){
        this.code = code;
        this.errMsg = errMsg;
    }

    @Override
    public String toString() {
        return "CustomTypeException{" +
                "code=" + code +
                ", errMsg='" + errMsg + '\'' +
                '}';
    }
}
