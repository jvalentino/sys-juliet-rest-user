package com.github.jvalentino.juliet.config

import groovy.transform.CompileDynamic
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Used to tell swagger to include X-Auth-Token on every request
 * @author john.valentino
 */
@Configuration
@CompileDynamic
@SuppressWarnings(['DuplicateStringLiteral'])
class SwaggerConfiguration {

    @Value('${spring.application.name}')
    String appName

    // https://stackoverflow.com/questions/63671676/
    // springdoc-openapi-ui-add-jwt-header-parameter-to-generated-swagger
    @Bean
    OpenAPI customOpenAPI() {
        new OpenAPI()
                .info(new Info().title(appName).version('1.0.0'))
                .components(new Components()
                        .addSecuritySchemes('X-Auth-Token', new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name('X-Auth-Token')))
                .addSecurityItem(new SecurityRequirement().addList('X-Auth-Token'))
    }

}
