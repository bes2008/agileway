package com.jn.agileway.redis.examples.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@EnableKnife4j
@EnableConfigurationProperties(Knife4jProperties.class)
public class Knife4jConfig {
    /****************************************************************************
     *  Manager API
     ****************************************************************************/

    @Bean
    public Docket managerRestAPi(Knife4jProperties swaggerProperties) {

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(managerApiInfo(swaggerProperties))
                .select()
                .apis(RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage()))
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .groupName("default");
    }

    private ApiInfo managerApiInfo(Knife4jProperties swaggerProperties) {
        return new ApiInfoBuilder()
                .title(swaggerProperties.getApplicationTitle())
                .description("https://github.com/fangjinuo  http://www.cnblogs.com/f1194361820")
                .termsOfServiceUrl("https://github.com/fangjinuo")
                .contact("fs1194361820@163.com")
                .version("2.0")
                .build();

    }

}
