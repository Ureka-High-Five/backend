package org.highfive.backend.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;


@OpenAPIDefinition(
        info = @Info(
                title = "LEAD:ME OTT API",
                version = "v1",
                description = "HighFive OTT Swagger 문서 입니다."
        ),

        servers = {
                @Server(url = "https://api.lead-me.site", description = "Deploy Server URL"),
                @Server(url = "http://localhost:8080", description = "Local Host URL")}
)
@Configuration
public class SwaggerConfig {

        private static final String ACCESS_TOKEN_KEY = "Access Token (Bearer)";

        @Bean
        public OpenAPI openAPI() {
                return new OpenAPI()
                        .components(createComponents())
                        .addSecurityItem(createSecurityRequirement())
                        .info(createApiInfo());
        }

        private Components createComponents() {
                return new Components()
                        .addSecuritySchemes(ACCESS_TOKEN_KEY, createAccessTokenSecurityScheme());
        }

        private SecurityRequirement createSecurityRequirement() {
                return new SecurityRequirement()
                        .addList(ACCESS_TOKEN_KEY);
        }

        private SecurityScheme createAccessTokenSecurityScheme() {
                return new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("Authorization")
                        .in(SecurityScheme.In.HEADER)
                        .name(HttpHeaders.AUTHORIZATION);
        }

        private io.swagger.v3.oas.models.info.Info createApiInfo() {
                return new io.swagger.v3.oas.models.info.Info()
                        .title("lead-me API")
                        .description("lead-me API 명세서")
                        .version("1.0.0");
        }
}
