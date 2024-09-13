package com.zkh.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.zkh.constant.AuthenticationConstants;
import com.zkh.model.SecurityUserDetails;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TokenTransferFilter extends OncePerRequestFilter {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //获取token  规定前端传token是通过header的Authorization值 "bearer "+token
//        String requestToken = (String) request.getAttribute(AuthenticationConstants.AUTHORIZATION); 错
        String requestToken = request.getHeader(AuthenticationConstants.AUTHORIZATION);
        String realToken = requestToken.replaceFirst(AuthenticationConstants.BEARER, "");
        //从redis中将token对应的UserDetails对象取出来
        //            stringRedisTemplate.opsForValue().set(AuthenticationConstants.TOKEN_PREFIX + token,
        //                    jsonUserString,
        //                    Duration.ofSeconds(AuthenticationConstants.EXPIRE_TIME));
        //判断realToken是否有值
        String tokenKey = AuthenticationConstants.TOKEN_PREFIX + realToken;
        if(StringUtils.hasText(realToken) && stringRedisTemplate.hasKey(tokenKey)){
            //token续签
            if (stringRedisTemplate.getExpire(tokenKey)<AuthenticationConstants.TOKEN_EXPIRE_THRESHOLD_TIME){
                //token时间过少，有可能导致当前业务提交时发现token过期了，所以当他访问时经过这个模块了就给他续签4个小时
                stringRedisTemplate.expire(tokenKey, Duration.ofSeconds(AuthenticationConstants.EXPIRE_TIME));
            }

            //redis中也有这个token,则需要取出，redis对应key的value，即是封装的Security认识的UserDetails对象
            String jsonString = stringRedisTemplate.opsForValue().get(AuthenticationConstants.TOKEN_PREFIX + realToken);
            //使用jackson转换json string 为 Object
            ObjectMapper objectMapper = new ObjectMapper();
            Principal principal = objectMapper.readValue(jsonString, Principal.class);
            //转换为我们自己封装的继承了Security框架的UserDetails类的对象
            SecurityUserDetails securityUserDetails = new SecurityUserDetails();
            //将封装的继承了Security框架的UserDetails类的对象返回给框架

            // 处理权限、获取权限,使用set的目的，去重。
//            Set<SimpleGrantedAuthority> collect = securityUserDetails.getPerms().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
            Set<SimpleGrantedAuthority> collectPerms = securityUserDetails.getPerms().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
            // 创建UsernamePasswordAuthenticationToken对象
//            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(securityUserDetails,null,collect);
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(securityUserDetails, null, collectPerms);
            // 将认证用户对象存放到当前模块的上下方中
//            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(usernamePasswordAuthenticationToken);
        }
        filterChain.doFilter(request,response);
    }
}
