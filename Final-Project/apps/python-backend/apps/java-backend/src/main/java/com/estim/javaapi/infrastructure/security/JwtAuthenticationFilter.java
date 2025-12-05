package com.estim.javaapi.infrastructure.security;

import com.estim.javaapi.domain.user.UserId;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;

    public JwtAuthenticationFilter(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
        throws ServletException, IOException {

        try {
            String authorizationHeader = request.getHeader("Authorization");

            // Debug 1: see the header
            // (You can remove these logs once everything works.)
            System.out.println("[JWT] Incoming request " + request.getMethod() + " " + request.getRequestURI());
            System.out.println("[JWT] Authorization header = " + authorizationHeader);

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring("Bearer ".length()).trim();

                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    try {
                        UserId userId = jwtTokenService.parseUserIdFromAccessToken(token);

                        AuthenticatedUser principal = new AuthenticatedUser(userId);

                        UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                principal,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER"))
                            );

                        authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                        );

                        // Spring Security context
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        // Your custom thread-local context for controllers that use it
                        SecurityContext.setCurrentUser(principal);

                        System.out.println("[JWT] Authenticated user " + userId.value());
                    } catch (IllegalArgumentException ex) {
                        // Invalid / expired / malformed token: leave request unauthenticated
                        System.out.println("[JWT] Token validation failed: " + ex.getMessage());
                        SecurityContext.clear();
                    }
                }
            } else {
                // No Bearer header -> ensure thread-local is clear
                SecurityContext.clear();
            }

            filterChain.doFilter(request, response);
        } finally {
            // Make sure we don’t leak the user between requests on the same thread
            SecurityContext.clear();
        }
    }
}
