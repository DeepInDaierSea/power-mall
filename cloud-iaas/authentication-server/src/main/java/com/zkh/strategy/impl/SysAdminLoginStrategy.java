package com.zkh.strategy.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.zkh.constant.AuthenticationConstants;
import com.zkh.domain.LoginSysUser;
import com.zkh.domain.SecurityUserDetails;
import com.zkh.mapper.LoginSysUserMapper;
import com.zkh.strategy.LoginStrategy;
import com.zkh.utils.SetSecurityUserDetailsUtil;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;

@Service(AuthenticationConstants.SYS_ADMIN_LONGIN)
public class SysAdminLoginStrategy implements LoginStrategy {
    //注入mapper
    @Resource
    private LoginSysUserMapper loginSysUserMapper;

    @Override
    public UserDetails loadUserDetailsByStrategy(String username) {
        //根据前端传过来的username查询后台sysUser的记录，并且创建一个安全框架能够认识的UserDetails对象
//        LoginSysUser loginSysUser = loginSysUserMapper.selectOneByUsername(username);
        /**使用mybatisPlus的方法*/


        LoginSysUser loginSysUser = loginSysUserMapper.selectOne(new LambdaQueryWrapper<LoginSysUser>().eq(LoginSysUser::getUsername, username));
        //查询loginSysUser拥有的权限，表设计是sysUser依赖于角色sysUserRole表，角色表依赖于权限sys_表
        if (ObjectUtils.isNotNull(loginSysUser)){
            //查询用户权限，先查询用户的角色，通过角色查询权限
            Set<String> permissions = loginSysUserMapper.selectUserPerms(loginSysUser.getUserId());
            //创建安全对象
            SecurityUserDetails securityUserDetails = SetSecurityUserDetailsUtil.setSecurityUserDetails(loginSysUser);
            //判断permissions是否有值
            if (CollectionUtils.isNotEmpty(permissions) && permissions.size()!=0){
                //权限列表正常，给安全用户对象赋予权限
                securityUserDetails.setPerms(permissions);
                return securityUserDetails;
            }
        }

        return null;
    }


}
