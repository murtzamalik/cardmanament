package com.cms.config;

import com.cms.common.security.CorsHeaderFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class CorsConfig {

    /**
     * Comma-separated origins (exact URLs), e.g.
     * http://localhost:3000,http://46.224.146.158:7071
     * <p>
     * Override in Docker / prod: {@code APP_CORS_ALLOWED_ORIGINS}
     */
    @Value("${app.cors.allowed-origins:http://localhost:3000,http://127.0.0.1:3000}")
    private String allowedOrigins;

    @Bean
    public CorsHeaderFilter corsHeaderFilter() {
        return new CorsHeaderFilter(allowedOrigins);
    }

    @Bean
    public FilterRegistrationBean<CorsHeaderFilter> corsHeaderFilterRegistration(CorsHeaderFilter filter) {
        FilterRegistrationBean<CorsHeaderFilter> reg = new FilterRegistrationBean<>(filter);
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE);
        reg.addUrlPatterns("/*");
        return reg;
    }

    /**
     * Patterns for Spring Security CORS — must stay in sync with {@link CorsHeaderFilter} rules.
     */
    private List<String> allowedOriginPatterns() {
        Set<String> patterns = new LinkedHashSet<>();
        Arrays.stream(allowedOrigins.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .forEach(patterns::add);
        // Dev: any port on localhost / 127.0.0.1
        patterns.add("http://localhost:*");
        patterns.add("https://localhost:*");
        patterns.add("http://127.0.0.1:*");
        patterns.add("https://127.0.0.1:*");
        return new ArrayList<>(patterns);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(allowedOriginPatterns());
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"));
        config.setAllowedHeaders(List.of(
            "Authorization", "Content-Type", "Accept", "Accept-Language",
            "X-Requested-With", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"
        ));
        config.setExposedHeaders(List.of("Authorization", "Content-Type"));
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
