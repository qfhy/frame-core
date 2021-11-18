package com.wdbgxs.www.exception;


public enum CustomExceptionInfoEnums {

    PACKAGE_IS_NULL(-1000,"包路径不可为空"),
    PACKAGE_NOT_EXISTS(-1001,"包路径不存在"),
    ANNOTATION_NOT_ALLOWED_COEXIST(-1002,"Autowired和Resource注解不可共用"),
    IOC_OBJECT_INJECTION_FAIL(-1003,"自动注入失败");

    private int code;

    private String msg;

    private CustomExceptionInfoEnums(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
