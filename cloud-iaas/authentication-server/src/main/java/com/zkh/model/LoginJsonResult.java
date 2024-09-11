package com.zkh.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 统一登录响应结果对象类
 */
@Data
@AllArgsConstructor
public class LoginJsonResult {
    /**
     *
     */
    private String tokenJsonObject;
    /**
     * token有效时长
     */
    private Long expireTime;
}
