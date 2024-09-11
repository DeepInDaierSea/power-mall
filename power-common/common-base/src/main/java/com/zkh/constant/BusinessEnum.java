package com.zkh.constant;


public enum BusinessEnum {
    UN_AUTHENTICATION(403,"未认证"),
    SERVER_INNER_ERROR(500,"服务器内部错误"),
    ACCESS_DENY(405,"访问被拒绝"),
    UN_AUTHORIZATION(403,"没有权限访问"),
    USERNAME_NOTFOUND(404,"用户不存在"),
    ACCOUNT_LOCKED(406, "账户被锁定，暂时无法服务"),
    CREDENTIALS_EXPIRE(407, "凭证过期,请重新登录"),
    OPTION_FAIL(408, "操作失败");
    BusinessEnum(Integer code,String msg){
        this.code = code;
        this.msg = msg;
    }
    private Integer code;
    private String msg;

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
