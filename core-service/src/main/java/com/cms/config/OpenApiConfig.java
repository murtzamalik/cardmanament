package com.cms.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//Below adding new imports
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;


@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
//        return new OpenAPI()
//            .info(new Info()
//                .title("Card Management System API")
//                .version("1.0")
//                .description("REST API for Card Management System"));
        //Below adding new code for authorize in swagger UI
        return new OpenAPI()
                .info(new Info()
                        .title("Card Management System API")
                        .version("1.0")
                        .description("REST API for Card Management System"))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Paste your JWT token here (without Bearer prefix)")));

    }
}
