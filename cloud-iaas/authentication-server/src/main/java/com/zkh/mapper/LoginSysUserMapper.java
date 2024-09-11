package com.zkh.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zkh.domain.LoginSysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

@Mapper
public interface LoginSysUserMapper extends BaseMapper<LoginSysUser> {
    Set<String> selectUserPerms(@Param("userId") Long userId);
//    LoginSysUser selectOneByUsername(String username);    使用mybatisPlus自定义的方法selectOne
}