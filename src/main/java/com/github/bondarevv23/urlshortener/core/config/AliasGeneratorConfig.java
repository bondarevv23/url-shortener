package com.github.bondarevv23.urlshortener.core.config;

import com.github.bondarevv23.urlshortener.core.service.AliasGeneratorService;
import com.github.bondarevv23.urlshortener.core.service.AliasGeneratorServiceImpl;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "alias")
@Getter
@Setter
public class AliasGeneratorConfig {

    private int size;

    @Bean
    public AliasGeneratorService aliasGeneratorService() {
        return new AliasGeneratorServiceImpl(size);
    }
}
