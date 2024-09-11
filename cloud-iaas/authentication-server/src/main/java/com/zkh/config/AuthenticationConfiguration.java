package com.zkh.config;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zkh.constant.AuthenticationConstants;
import com.zkh.constant.BusinessEnum;
import com.zkh.constant.HttpResponseConstants;
import com.zkh.impl.UserDetailServiceImpl;
import com.zkh.model.LoginJsonResult;
import com.zkh.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.UUID;

@Configuration
public class AuthenticationConfiguration extends WebSecurityConfigurerAdapter {
    @Resource
    private UserDetailServiceImpl userDetailServiceImpl;
    @Resource
    private AuthenticationSuccessHandler authenticationSuccessHandler;
    @Resource
    private LogoutSuccessHandler logoutSuccessHandler;
    @Resource
    private AuthenticationFailureHandler authenticationFailureHandler;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //使得框架走我们自己声明的认证流程
        auth.userDetailsService(userDetailServiceImpl);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //关闭跨站请求伪造
        http.cors().disable();
        //关闭跨域请求
        http.csrf().disable();
        //关闭session使用策略
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        //配置登录
        http.formLogin()
                .loginProcessingUrl(AuthenticationConstants.LOGIN_URL)
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler)
                .permitAll();
        //配置等出
        http.logout()
                .logoutSuccessUrl(AuthenticationConstants.LOGOUT_URL).
                logoutSuccessHandler(logoutSuccessHandler)
                .permitAll();
        //要求所有请求都必须认证
        http.authorizeRequests().anyRequest().authenticated();
    }

    //配置登陆成功处理器
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            //登陆成功应该做什么事情
            /**1、获取登录请求的用户 */
            //设置响应头
//            response.setHeader(HttpResponseConstants.CONTENT_TYPE,HttpResponseConstants.APPLICATION_JSON);
            response.setContentType(HttpResponseConstants.CONTENT_TYPE);
            response.setCharacterEncoding(HttpResponseConstants.CHARACTER_ENCODING);
            /**2、颁发token*/
            String token = UUID.randomUUID().toString();
            Object principal = authentication.getPrincipal();
            //安全框架对象，转换成json
            String jsonUserString = JSONObject.toJSONString(principal);
            //放置于redis中
            stringRedisTemplate.opsForValue().set(AuthenticationConstants.TOKEN_PREFIX + token,
                    jsonUserString,
                    Duration.ofSeconds(AuthenticationConstants.EXPIRE_TIME));
            //封装一个统一登录响应对象
            LoginJsonResult jsonResult = new LoginJsonResult(token, AuthenticationConstants.EXPIRE_TIME);
            //封装结果对象
            Result<LoginJsonResult> successResult = Result.success(jsonResult);
            //封装返回给前端的对象为json格式
            ObjectMapper objectMapper = new ObjectMapper();
            String string = objectMapper.writeValueAsString(successResult);

            //响应给前端
            PrintWriter writer = response.getWriter();
            writer.write(string);
            writer.flush();
            writer.close();
        };
    }

    //配置登陆失败处理器
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) -> {
            //登录失败响应给前端提示信息
            response.setContentType(HttpResponseConstants.CONTENT_TYPE);
            response.setCharacterEncoding(HttpResponseConstants.CHARACTER_ENCODING);
            Result<Object> result = new Result<>();
            //判断异常类型
            if (exception instanceof UsernameNotFoundException) {
                result = result.fail(BusinessEnum.USERNAME_NOTFOUND);
            } else if (exception instanceof BadCredentialsException) {
                result = result.fail(BusinessEnum.UN_AUTHORIZATION);
            } else if (exception instanceof LockedException) {
                result = result.fail(BusinessEnum.ACCOUNT_LOCKED);
            } else if (exception instanceof CredentialsExpiredException) {
                result = result.fail(BusinessEnum.CREDENTIALS_EXPIRE);
            } else {
                result = result.fail(BusinessEnum.OPTION_FAIL);
            }
            //封装响应结果
            ObjectMapper objectMapper = new ObjectMapper();
            String resultString = objectMapper.writeValueAsString(result);

            PrintWriter writer = response.getWriter();
            writer.write(resultString);
            writer.flush();
            writer.close();
        };
    }

    //配置登出成功处理器
    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return (request, response, authentication) -> {
            //登出成功应该将redis中的凭证删除掉
            //获取token
            String bearerToken = (String) request.getAttribute(AuthenticationConstants.AUTHORIZATION);
            String token = bearerToken.replaceFirst(AuthenticationConstants.BEARER, "");
            //删除redis的token
            Boolean delete = stringRedisTemplate.delete(AuthenticationConstants.TOKEN_PREFIX + token);
            //设置响应头
            response.setContentType(HttpResponseConstants.CONTENT_TYPE);
            response.setCharacterEncoding(HttpResponseConstants.CHARACTER_ENCODING);
            PrintWriter writer = response.getWriter();
//            if (!delete){
//                //删除失败
//            }
            Result<Object> result = Result.success(null);
            ObjectMapper objectMapper = new ObjectMapper();
            String valueAsString = objectMapper.writeValueAsString(result);
            writer.write(valueAsString);
            writer.flush();
            writer.close();
        };
    }

    //使用BCryptPasswordEncoder
    @Bean
    public BCryptPasswordEncoder getBCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
