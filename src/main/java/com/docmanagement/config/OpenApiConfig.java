package com.docmanagement.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI documentManagementOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Document Management API")
                .description(
                    "REST API for document and folder management, including file upload/download via GridFS.")
                .version("1.0.0"));
  }
}
