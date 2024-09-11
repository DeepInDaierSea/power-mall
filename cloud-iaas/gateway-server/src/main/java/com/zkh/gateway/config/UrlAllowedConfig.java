package com.zkh.gateway.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.List;

@Configuration
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@RefreshScope       //允许随着配置文件的刷新而动态刷新这个值
@ConfigurationProperties(prefix = "gateway.white")
public class UrlAllowedConfig {
    public List<String> urlAllowed;
    public List<String> urlAllowedA;
}
