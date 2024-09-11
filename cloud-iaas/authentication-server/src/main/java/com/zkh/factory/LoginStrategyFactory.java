package com.zkh.factory;

import com.zkh.strategy.LoginStrategy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
public class LoginStrategyFactory {

    @Resource
    private Map<String,LoginStrategy> loginStrategyMap = new HashMap<>();


    /**通过loginType返回不同的strategy实现类
     * @param loginType
     * @return
     */
    public LoginStrategy loadLoginStrategyInstance(String loginType){
        return loginStrategyMap.get(loginType);
    }
}
