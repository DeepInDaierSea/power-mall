package com.zkh.strategy;

import org.springframework.security.core.userdetails.UserDetails;


public interface LoginStrategy {
    UserDetails loadUserDetailsByStrategy(String username);
}
