package com.omnixys.address.security.http;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityHttpHeadersFilterTest {

    private final SecurityHttpHeadersFilter filter = new SecurityHttpHeadersFilter();

    @Test
    void appliesHelmetEquivalentHeaders() throws Exception {
        var request = new MockHttpServletRequest("GET", "/health/readiness");
        request.setSecure(true);
        var response = new MockHttpServletResponse();
        FilterChain chain = (req, res) -> {
        };

        filter.doFilter(request, response, chain);

        assertEquals(SecurityHttpHeadersFilter.CONTENT_SECURITY_POLICY,
                response.getHeader("Content-Security-Policy"));
        assertTrue(response.getHeader("Strict-Transport-Security").contains("max-age=31536000"));
        assertEquals("nosniff", response.getHeader("X-Content-Type-Options"));
        assertEquals("SAMEORIGIN", response.getHeader("X-Frame-Options"));
        assertEquals("Omnixys", response.getHeader("X-Powered-By"));
    }

    @Test
    void doesNotOverrideExistingHeaders() throws Exception {
        var request = new MockHttpServletRequest("GET", "/health/readiness");
        var response = new MockHttpServletResponse();
        response.setHeader("X-Frame-Options", "DENY");
        FilterChain chain = (req, res) -> {
        };

        filter.doFilter(request, response, chain);

        assertEquals("DENY", response.getHeader("X-Frame-Options"));
    }

    @Test
    void hstsOnlyOnSecureRequests() throws Exception {
        var request = new MockHttpServletRequest("GET", "/health/readiness");
        request.setSecure(false);
        var response = new MockHttpServletResponse();
        FilterChain chain = (req, res) -> {
        };

        filter.doFilter(request, response, chain);

        assertNull(response.getHeader("Strict-Transport-Security"));
    }
}
