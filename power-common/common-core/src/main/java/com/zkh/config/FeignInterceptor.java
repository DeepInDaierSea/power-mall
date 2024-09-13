package com.zkh.config;

/**
 * feign拦截器，解决远程调用没有token的问题
 */

import com.zkh.constant.AuthenticationConstants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        //获取当前上下文对象
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String requestToken = requestAttributes.getRequest().getHeader(AuthenticationConstants.AUTHORIZATION);
        //判断是否有值
        if (StringUtils.hasText(requestToken)){
//            String token = header.replaceFirst(AuthenticationConstants.BEARER, "");
            requestTemplate.header(AuthenticationConstants.AUTHORIZATION, requestToken);
            return;
        }
        //若是没有token，比如第三方的请求啥的，不能让人家不能请求吧，所以给他设置一个永久能用的token,TOKEN的ttl为-1即永久有效
        requestTemplate.header(AuthenticationConstants.AUTHORIZATION,AuthenticationConstants.BEARER+"ab2838ac-14c6-4962-b2d5-788fba1c5837");
    }
}
