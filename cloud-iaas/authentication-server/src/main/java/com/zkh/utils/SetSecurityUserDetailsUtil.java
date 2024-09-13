package com.zkh.utils;

import com.zkh.domain.LoginSysUser;
import com.zkh.model.SecurityUserDetails;

public class SetSecurityUserDetailsUtil {
    public static SecurityUserDetails setSecurityUserDetails(LoginSysUser loginSysUser){
        SecurityUserDetails securityUserDetails = new SecurityUserDetails();
        securityUserDetails.setUserId(loginSysUser.getUserId());
        securityUserDetails.setPassword(loginSysUser.getPassword());
        securityUserDetails.setUsername(loginSysUser.getUsername());
        securityUserDetails.setShopId(loginSysUser.getShopId());
        securityUserDetails.setStatus(loginSysUser.getStatus());
        return securityUserDetails;
    }
}
