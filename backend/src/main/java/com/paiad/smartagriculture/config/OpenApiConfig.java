package com.paiad.smartagriculture.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.models.GroupedOpenApi;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("智慧农业后端接口文档")
                        .description("基于 JDK 21 + Spring Boot 3.3.6 重构的接口文档")
                        .contact(new Contact().name("AD").email("paiad314@163.com"))
                        .version("1.0"));
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("用户模块")
                .pathsToMatch("/user/**")
                .build();
    }

    @Bean
    public GroupedOpenApi businessApi() {
        return GroupedOpenApi.builder()
                .group("业务模块")
                .pathsToMatch("/device/**", "/env-data/**", "/control-command/**", "/alarm/**", "/alarm-rule/**")
                .build();
    }
}
