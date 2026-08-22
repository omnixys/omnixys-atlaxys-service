package com.omnixys.address.security.http;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Helmet-Parität (siehe TS security/http/helmet.handler.ts):
 * CSP, HSTS, nosniff, Frame-Guard und verstecktes X-Powered-By.
 * Bestehende Header (z.B. von Spring Security) werden nicht überschrieben.
 */
@Component
public class SecurityHttpHeadersFilter extends OncePerRequestFilter {

    public static final String CONTENT_SECURITY_POLICY =
            "default-src 'self' https:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https:; img-src 'self' data:";

    private static final long HSTS_MAX_AGE = 31536000; // 1 Jahr in Sekunden

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        setIfAbsent(response, "Content-Security-Policy", CONTENT_SECURITY_POLICY);

        if (request.isSecure()) {
            setIfAbsent(response, "Strict-Transport-Security",
                    "max-age=" + HSTS_MAX_AGE + "; includeSubDomains; preload");
        }

        setIfAbsent(response, "X-Content-Type-Options", "nosniff");
        setIfAbsent(response, "X-Frame-Options", "SAMEORIGIN");
        setIfAbsent(response, "X-Powered-By", "Omnixys");

        filterChain.doFilter(request, response);
    }

    private static void setIfAbsent(HttpServletResponse response, String name, String value) {
        if (response.getHeader(name) == null) {
            response.setHeader(name, value);
        }
    }
}
