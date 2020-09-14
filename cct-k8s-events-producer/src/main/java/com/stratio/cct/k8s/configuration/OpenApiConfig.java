/*
 * © 2019 Stratio Big Data Inc., Sucursal en España. All rights reserved.
 *
 * This software – including all its source code – contains proprietary information of Stratio Big Data Inc., Sucursal
 * en España and may not be revealed, sold, transferred, modified, distributed or otherwise made available, licensed or
 * sublicensed to third parties; nor reverse engineered, disassembled or decompiled, without express written
 * authorization from Stratio Big Data Inc., Sucursal en España.
 */

package com.stratio.cct.k8s.configuration;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.stratio.cct.k8s.adapter.web.ErrorResponse;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig implements WebMvcConfigurer {

  private static final String API_DOCS_CONTEXT_PATH = "${springdoc.api-docs.context-path}";

  private static final String AUTH_COOKIE_NAME = "dcos-acs-auth-cookie";

  @Bean
  public OpenAPI openApi(@Value("${springdoc.version}") String appVersion,
      @Value(API_DOCS_CONTEXT_PATH) String apiDocsContextPath) {
    return new OpenAPI()
        .info(new Info()
            .title("K8s Events Producer API")
            .version(appVersion)
            .description(
                "K8s Events Producer")
            .contact(new Contact()
                .email("command-center@stratio.com")
                .name("StratioBD")
                .url("https://www.stratio.com")))
        .servers(Collections.singletonList(new Server().url(StringUtils.appendIfMissing(apiDocsContextPath, "/"))))
        .components(new Components()
            .addSchemas("ErrorResponse",
                new Schema<ErrorResponse>().example(
                    new ErrorResponse(Date.from(LocalDate.of(2020, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC)), 0,
                        "error", "exception", "message", "/path")))
            .addResponses("400BadRequest", new ApiResponse()
                .description("Bad Request")
                .content(defaultErrorContent()))
            .addResponses("401Unauthorized", new ApiResponse()
                .description("Unauthorized")
                .content(defaultErrorContent()))
            .addResponses("403Forbidden", new ApiResponse()
                .description("Forbidden")
                .content(defaultErrorContent()))
            .addResponses("404NotFound", new ApiResponse()
                .description("Resource not found")
                .content(defaultErrorContent()))
            .addResponses("default", new ApiResponse()
                .description("Unexpected error")
                .content(defaultErrorContent()))
            .addSecuritySchemes(AUTH_COOKIE_NAME,
                new SecurityScheme().type(Type.APIKEY).in(In.COOKIE).name(AUTH_COOKIE_NAME)))
        .paths(new Paths())
        .security(Collections
            .singletonList(new SecurityRequirement().addList(AUTH_COOKIE_NAME, Collections.emptyList())));
  }

  private Content defaultErrorContent() {
    return new Content()
        .addMediaType(MediaType.APPLICATION_JSON_VALUE,
            new io.swagger.v3.oas.models.media.MediaType()
                .schema(new Schema<ErrorResponse>()
                    .$ref(ErrorResponse.class.getSimpleName())));
  }

}