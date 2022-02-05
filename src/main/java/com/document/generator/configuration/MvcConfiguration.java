package com.document.generator.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * This is a configuration class. It configures a request for which you can get a signature and then display it in a pdf document
 * @author Artem Yakunin
 * @version 1.0
 */
@Configuration
public class MvcConfiguration implements WebMvcConfigurer {
    @Value("${signature.path}")
    private String signaturePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/api/v1/documents/signature/**")
                .addResourceLocations(signaturePath);
    }
}
