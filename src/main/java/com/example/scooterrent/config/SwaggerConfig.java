package com.example.scooterrent.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI springShopOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080");
        localServer.setDescription("本地开发环境");

        return new OpenAPI()
                .info(new Info()
                    .title("滑板车租赁系统 API")
                    .description("滑板车租赁系统的REST API文档")
                    .version("v1.0.0")
                    .license(new License()
                        .name("Apache 2.0")
                        .url("http://springdoc.org")))
                .servers(Arrays.asList(localServer));
    }
} 