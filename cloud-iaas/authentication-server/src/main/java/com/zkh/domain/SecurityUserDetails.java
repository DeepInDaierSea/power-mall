package com.zkh.domain;


import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
@Data
public class SecurityUserDetails implements UserDetails {
    //sysAdmin  属性;
    private Long userId;
    private String username;
    private String password;
    //账户status状态  0：禁用   1：正常
    private Integer status;
    private Long shopId;
    private Set<String> perms;
    //member 商城用户  属性
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.status == 1;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.status == 1;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.status == 1;
    }

    @Override
    public boolean isEnabled() {
        return this.status == 1;
    }

    
    //重写getPerms方法
    public Set<String> getPerms() {
        HashSet<String> finalPermissions = new HashSet<>();
        this.perms.forEach(perm -> {
            if (perm.contains(",")){
                String[] splitPerm = perm.split(",");
                for (String perm_1 : splitPerm) {
                    finalPermissions.add(perm_1);
                }
            }else {
                finalPermissions.add(perm);
            }
        });
        return finalPermissions;
    }
}
