package com.zkh.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.HashSet;

/**
 * swagger自动装配类
 */
@Configuration
@EnableConfigurationProperties(SwaggerProperties.class)
//Enable support for @ConfigurationProperties annotated beans. @ConfigurationProperties beans can be registered in the standard way (for example using @Bean methods) or, for convenience, can be specified directly on this annotation.
public class SwaggerAutoConfig {

    @Autowired
    private SwaggerProperties swaggerProperties;

    @Autowired
    private Environment environment;
    //spring的定义的对象，可以获取yaml中的spring元素


    @Bean
    public Docket docket() {
        Boolean flag = true;
        String[] activeProfiles = environment.getActiveProfiles();
        for (String activeProfile : activeProfiles) {
            if (activeProfile.equals("pro")) {      //如果是生产环境就不再生成api文档
                flag = false;
                break;
            }
        }
        return new Docket(DocumentationType.OAS_30)     //生成的文档类型
                .apiInfo(getApiInfo())            //api生成的信息包含些什么，在方法getApiInfo()中定义了
                .enable(flag)                           //是否生成api文档
                .select()
                .apis(RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage())).build();
    }

    private ApiInfo getApiInfo() {
        Contact contact = new Contact(
                swaggerProperties.getName(),
                swaggerProperties.getUrl(),
                swaggerProperties.getEmail()
        );
        ApiInfo apiInfo = new ApiInfo(
                swaggerProperties.getTitle(),
                swaggerProperties.getDescription(),
                swaggerProperties.getVersion(),
                swaggerProperties.getTermsOfServiceUrl(),
                contact,
                swaggerProperties.getLicense(),
                swaggerProperties.getLicenseUrl(),
                new HashSet<>()
        );

        return apiInfo;
    }
}
