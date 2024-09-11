package com.zkh.impl;

import com.zkh.constant.AuthenticationConstants;
import com.zkh.factory.LoginStrategyFactory;
import com.zkh.strategy.LoginStrategy;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;

@Component
public class UserDetailServiceImpl implements UserDetailsService {
    @Resource
    private LoginStrategyFactory loginStrategyFactory;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //获取请求头中的loginType，约定好的登录登陆终端辨识标识；
        //从RequestContextHolder、上下文对象中获取请求域对象
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //获取约定的登录登陆终端标识；
        String loginType = servletRequestAttributes.getRequest().getHeader(AuthenticationConstants.LONGIN_TYPE);
        //按逻辑是、应该判断是哪个登录终端，进而给出不同的登录响应；
//        if (loginType.equals(AuthenticationConstants.SYS_ADMIN_LONGIN)) {
//            //系统管理员登录流程
//        }else {
//            //商城用户登录流程
//        }

        //由于为了以后的扩展性，基于策略模式实现处理不同的登录请求
        /**
         * 判断请求头中是否有请求来源标识，若没有表示就表示这个请求是一个非法请求，未知请求
         * */
        if (!StringUtils.hasText(loginType)){
            //no loginType
            throw new InternalAuthenticationServiceException("非法登录、登录来源未知、登录类型不匹配");
        }
        //String SYS_ADMIN_LONGIN = "sysAdminLogin";
        //String SYS_MEMBER_LOGIN = "sysMemberLogin";
        LoginStrategy loginStrategyInstance = loginStrategyFactory.loadLoginStrategyInstance(loginType);
//        UserDetails userDetails = loginStrategy.loginByStrategy(loginType);
        return loginStrategyInstance.loadUserDetailsByStrategy(username);
    }
}
