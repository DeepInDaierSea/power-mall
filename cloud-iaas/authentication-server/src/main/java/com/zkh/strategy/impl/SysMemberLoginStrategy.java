package com.zkh.strategy.impl;

import com.zkh.constant.AuthenticationConstants;
import com.zkh.strategy.LoginStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service(AuthenticationConstants.SYS_MEMBER_LOGIN)
public class SysMemberLoginStrategy implements LoginStrategy {

    @Override
    public UserDetails loadUserDetailsByStrategy(String username) {
        return null;
    }
}
