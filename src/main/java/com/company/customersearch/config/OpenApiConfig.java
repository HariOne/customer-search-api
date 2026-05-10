package com.company.customersearch.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Development"),
                        new Server().url("https://api.production.com").description("Production")
                ))
                .info(new Info()
                        .title("Customer Search API")
                        .version("1.0.0")
                        .description("Production-grade REST API for searching customers across multiple brands")
                        .termsOfService("https://company.com/terms")
                        .contact(new Contact()
                                .name("API Support")
                                .email("support@company.com")
                                .url("https://company.com")
                        )
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")
                        )
                );
    }
}
