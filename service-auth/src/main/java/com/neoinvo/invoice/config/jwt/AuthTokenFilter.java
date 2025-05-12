package com.neoinvo.invoice.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public AuthTokenFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String path = request.getRequestURI();
            logger.debug("🔍 Incoming request path: {}", path);

            // Skip filtering on public auth endpoints
            if (path.startsWith("/auth/login") || path.startsWith("/auth/register")) {
                logger.debug("⏭️ Skipping JWT filter for public endpoint: {}", path);
                filterChain.doFilter(request, response);
                return;
            }

            String headerAuth = request.getHeader("Authorization");
            logger.debug("🪪 Authorization header: {}", headerAuth);

            String jwt = parseJwt(request);
            logger.debug("🧪 Parsed JWT: {}", jwt);

            if (jwt != null && jwtUtil.validateJwtToken(jwt)) {
                logger.debug("✅ JWT is valid");

                String email = jwtUtil.getUserEmailFromJwtToken(jwt);
                logger.debug("👤 Extracted email from token: {}", email);

                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.debug("🔐 SecurityContext updated with authenticated user: {}", email);
            } else {
                logger.warn("❌ Invalid or missing JWT token");
            }
        } catch (Exception e) {
            logger.error("🚫 Cannot set user authentication", e);
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}