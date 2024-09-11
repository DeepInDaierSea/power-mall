package com.zkh.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zkh.constant.AuthenticationConstants;
import com.zkh.constant.BusinessEnum;
import com.zkh.constant.HttpResponseConstants;
import com.zkh.model.Result;
import com.zkh.gateway.config.UrlAllowedConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Date;

@Component
@Slf4j
public class AuthenticationFilter implements GlobalFilter, Ordered {
//    获取nacos config中的url白名单
    @Resource
    private UrlAllowedConfig urlAllowedConfig;
//    注入StringRedisTemplate
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ObjectMapper objectMapper;


//    约定token置于request header 的 Authorization 中 bearer token
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        或缺请求路径
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();
        //判断path是否包含在访问白名单中
        if (urlAllowedConfig.getUrlAllowed().contains(path)){
            //path包含在访问白名单中
            return chain.filter(exchange);
        }
        //不在url白名单中，需要做校验
        //获取Authorization
        String Authorization = request.getHeaders().getFirst(AuthenticationConstants.AUTHORIZATION);
        //判断Authorization是否有值
        if (StringUtils.hasText(Authorization)){
            //有值，接着获取token,判断是否有值并且token存在于redis中
            //约定，token的key的格式为  "REDIS_KEY: "+token;
            String token = Authorization.replaceFirst(AuthenticationConstants.BEARER, "");
            if (StringUtils.hasText(token) && stringRedisTemplate.hasKey(AuthenticationConstants.TOKEN_PREFIX+token)){
                //redis中存在token
                return chain.filter(exchange);
            }
        }
        //到这一步表示redis没有这个token，记录日志
        log.error("请求被拦截，时间:{} URL:{}",new Date(),path);
        //获取response和header，利用response响应给前端错误信息
        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders headers = response.getHeaders();
        //设置响应头参数
        headers.set(HttpResponseConstants.CONTENT_TYPE,HttpResponseConstants.APPLICATION_JSON);
        //TODO
        //响应错误信息,约定使用HttpResponseConstants类作为响应对象、业务统一响应枚举类对象BusinessEnum
//        Result<Object> result = new Result<>(BusinessEnum.UN_Authorization.getCode(),BusinessEnum.UN_Authorization.getMsg(),null);
        Result<Object> fail = Result.fail(BusinessEnum.UN_AUTHORIZATION);
        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(fail);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        DataBuffer wrap = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(wrap));
    }


    @Override
    public int getOrder() {
        return -5;
    }
}
