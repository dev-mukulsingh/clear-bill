package com.clearbillsolutions.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI clearBillOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ClearBill Solutions | Smart Invoice and Billing Manager")
                        .description("REST API documentation for ClearBill Solutions: Smart Invoice and Billing Manager. Accurate Accounts, Honest Bills")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("ClearBill Support")
                                .email("billing@clearbillsolutions.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")));
    }
}
