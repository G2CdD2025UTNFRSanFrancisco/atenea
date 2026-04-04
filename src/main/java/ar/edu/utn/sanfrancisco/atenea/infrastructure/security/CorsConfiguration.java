package ar.edu.utn.sanfrancisco.atenea.infrastructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfiguration {

    @Value("${atenea.cors.allowed-origins:http://localhost:3000}")
    private String allowedOriginsConfig;

    @Value("${atenea.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String allowedMethodsConfig;

    @Value("${atenea.cors.allowed-headers:*}")
    private String allowedHeadersConfig;

    @Value("${atenea.cors.max-age:3600}")
    private Long maxAge;

    @Value("${atenea.cors.allow-credentials:true}")
    private Boolean allowCredentials;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();

        List<String> allowedOrigins = Arrays.asList(this.allowedOriginsConfig.split(","));
        configuration.setAllowedOrigins(allowedOrigins);

        List<String> allowedMethods = Arrays.asList(this.allowedMethodsConfig.split(","));
        configuration.setAllowedMethods(allowedMethods);

        if ("*".equals(this.allowedHeadersConfig)) {
            configuration.setAllowedHeaders(Arrays.asList("*"));
        } else {
            configuration.setAllowedHeaders(Arrays.asList(this.allowedHeadersConfig.split(",")));
        }

        configuration.setMaxAge(this.maxAge);
        configuration.setAllowCredentials(this.allowCredentials);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}

