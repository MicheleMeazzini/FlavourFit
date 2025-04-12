package com.example.demo.filter;

import com.example.demo.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;


public class AuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public AuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // filter only works for path beginning with "/api"
        if (!request.getRequestURI().startsWith("/api")){
            filterChain.doFilter(request, response);
            return;
        }

        if (request.getHeader("Auth") != null && request.getHeader("Auth").startsWith("Bearer ")) {
            String token = request.getHeader("Auth").substring(7);
            if (jwtUtil.isTokenExpired(token)) {
                // Token expired or invalid
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
                response.setContentType("application/json"); // Imposta il tipo di contenuto
                response.getWriter().write("{\"message\": \"Unauthorized - Invalid or expired token\"}");
                return;
            }
        } else {
            // Missing Token
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            response.setContentType("application/json"); // Imposta il tipo di contenuto
            response.getWriter().write("{\"message\": \"Unauthorized - Missing token\"}");
            return;
        }


        filterChain.doFilter(request, response);
    }
}
