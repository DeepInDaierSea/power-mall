package com.zkh.constant;

import org.springframework.stereotype.Component;

@Component
public interface HttpResponseConstants {

    /**
     * response响应头设置的参数    "content-type"
     */
    String CONTENT_TYPE = "content-type";

    /**
     * response响应头设置的响应类型    "application/json;charset=utf-8"
     */
    String APPLICATION_JSON = "application/json;charset=utf-8";


    /**
     * 响应字符集常量
     */
    String CHARACTER_ENCODING = "utf-8";
}
