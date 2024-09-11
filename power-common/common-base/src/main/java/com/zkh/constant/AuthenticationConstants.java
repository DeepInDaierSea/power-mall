package com.zkh.constant;

import org.springframework.stereotype.Component;

@Component
public interface AuthenticationConstants {
    /**
    * 约定前端将token放入request的header的Authentication中，这个类用来规定常量
    * */
    String AUTHORIZATION = "Authorization";
    /**
     * 约定前端传Authentication中token时的前缀:       "bearer " + token
     */
    String BEARER = "bearer ";
    /**
     * 约定的token存储redis时的前缀:         "REDIS_KEY: " + token
     */
    String TOKEN_PREFIX = "REDIS_KEY: ";

    /**
     * 登录uri
     */
    String LOGIN_URL = "/doLogin";
    /**
     * 登出uri
     */
    String LOGOUT_URL = "/doLogout";

    /**
     * 登录类型参数常量
     */
    String LONGIN_TYPE = "loginType";
    /**
     * 系统管理员登录标识
     */
    String SYS_ADMIN_LONGIN = "sysAdminLogin";
    /**
     * 商城用户登录
     */
    String SYS_MEMBER_LOGIN = "sysMemberLogin";
    /**
     * TOKEN 过期时间
     */
    long EXPIRE_TIME = 14400L;

}
