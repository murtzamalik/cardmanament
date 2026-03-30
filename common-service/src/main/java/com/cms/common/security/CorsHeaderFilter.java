package com.cms.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Runs early to add CORS headers to every response (including error responses).
 * Ensures browser receives CORS headers even when the request fails (e.g. 401).
 * Registered in CorsConfig with FilterRegistrationBean at HIGHEST_PRECEDENCE.
 */
public class CorsHeaderFilter extends OncePerRequestFilter {

    private final String allowedOriginsConfig;

    public CorsHeaderFilter(String allowedOriginsConfig) {
        this.allowedOriginsConfig = allowedOriginsConfig != null && !allowedOriginsConfig.isBlank()
            ? allowedOriginsConfig
            : "http://localhost:3000,http://127.0.0.1:3000";
    }

    /** Returns true if origin is allowed: from config list or localhost/127.0.0.1 (any port). */
    private boolean isOriginAllowed(String origin) {
        if (origin == null || origin.isBlank()) return false;
        List<String> allowed = Arrays.stream(allowedOriginsConfig.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toList());
        if (allowed.isEmpty()) allowed = List.of("http://localhost:3000");
        if (allowed.contains(origin)) return true;
        // Allow any localhost or 127.0.0.1 origin (any port) for dev/local
        String lower = origin.toLowerCase();
        return lower.startsWith("http://localhost:") || lower.startsWith("https://localhost:")
            || lower.startsWith("http://127.0.0.1:") || lower.startsWith("https://127.0.0.1:");
    }

    private void addCorsHeaders(HttpServletResponse response, String origin) {
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS, HEAD");
        response.setHeader("Access-Control-Allow-Headers",
            "Authorization, Content-Type, Accept, Accept-Language, X-Requested-With, Origin, Access-Control-Request-Method, Access-Control-Request-Headers");
        response.setHeader("Access-Control-Expose-Headers", "Authorization, Content-Type");
        response.setHeader("Access-Control-Max-Age", "3600");
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String origin = request.getHeader("Origin");
        if (origin != null && !origin.isBlank() && isOriginAllowed(origin)) {
            addCorsHeaders(response, origin);
        }
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
