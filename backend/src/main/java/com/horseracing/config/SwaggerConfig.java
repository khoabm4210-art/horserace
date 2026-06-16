package com.horseracing.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI horseRacingOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Horse Racing Backend API")
                .description("REST API for Horse Racing Management System")
                .version("1.0.0"))
            .addSecurityItem(new SecurityRequirement().addList("bearer"))
            .getComponents()
            .addSecuritySchemes("bearer",
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT Token"))
            .getOpenAPI();
    }
}
