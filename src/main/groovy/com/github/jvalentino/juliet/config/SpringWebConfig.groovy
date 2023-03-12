package com.github.jvalentino.juliet.config

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.converter.ByteArrayHttpMessageConverter
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Another magical class required for JSPs and Spring Boot to work
 */
@EnableWebMvc
@Configuration
@Slf4j
@CompileDynamic
@SuppressWarnings(['UnnecessarySetter'])
class SpringWebConfig implements WebMvcConfigurer {

    ObjectMapper jsonMapper() {
        new CustomObjectMapper()
    }

    @Override
    void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new StringHttpMessageConverter())
        converters.add(new MappingJackson2HttpMessageConverter(jsonMapper()))

        // requires for prometheus endpoint
        StringHttpMessageConverter converter = new StringHttpMessageConverter()
        converter.setSupportedMediaTypes(Arrays.asList(
                MediaType.TEXT_PLAIN,
                new MediaType('application', 'openmetrics-text')))
        converters.add(converter)

        converters.add(new ByteArrayHttpMessageConverter())

        // No converter for [class java.lang.String] with preset Content-Type
        // 'application/openmetrics-text;version=1.0.0;charset=utf-8']
    }

}
