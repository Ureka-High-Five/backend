package org.highfive.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "HighFive OTT API",
                version = "v1",
                description = "HighFive OTT Swagger 문서 입니다."
        )
)
@Configuration
public class SwaggerConfig {
}
