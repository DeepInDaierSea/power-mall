package com.zkh.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zkh.constant.BusinessEnum;
import com.zkh.constant.HttpResponseConstants;
import com.zkh.contants.AllowUrlConstants;
import com.zkh.filter.TokenTransferFilter;
import com.zkh.model.Result;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)      //
public class ResourcesServerSecurityConfig extends WebSecurityConfigurerAdapter {
    @Resource
    private TokenTransferFilter tokenTransferFilter;
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //关闭跨域资源分享
        http.cors().disable();
        //关闭跨站伪造
        http.csrf().disable();
        //关闭session策略
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).disable();

        //异常处理器配置
        http.exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler());
        //请求资源匹配
        http.authorizeHttpRequests()
                .antMatchers(AllowUrlConstants.RESOURCE_ALLOW_URLS)
                .permitAll()
                .anyRequest().authenticated();
        //token处理,在UsernamePasswordAuthenticationFilter之前添加一个token转换过滤器，用于将gateway转发过来的token解析为一个Principle对象
        http.addFilterBefore(tokenTransferFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                //设置响应头信息
                response.setContentType(HttpResponseConstants.CONTENT_TYPE);
                response.setCharacterEncoding(HttpResponseConstants.CHARACTER_ENCODING);
                //返回响应信息
                Result<String> result = new Result<>();
                result.fail(BusinessEnum.UN_AUTHORIZATION);
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonResult = objectMapper.writeValueAsString(result);

                PrintWriter writer = response.getWriter();
                writer.write(jsonResult);
                writer.flush();
                writer.close();
            }
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandler() {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                //response
                response.setContentType(HttpResponseConstants.CONTENT_TYPE);
                response.setCharacterEncoding(HttpResponseConstants.CHARACTER_ENCODING);
                //
                Result<String> result = new Result<>();
                result.fail(BusinessEnum.ACCESS_DENY);
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonResult = objectMapper.writeValueAsString(result);
                PrintWriter writer = response.getWriter();
                writer.write(jsonResult);
                writer.flush();
                writer.close();
            }
        };
    }

}
