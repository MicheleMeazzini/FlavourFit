package com.example.demo.filter;

import com.example.demo.model.document.User;
import com.example.demo.repository.document.UserRepository;
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
    private final UserRepository userRepository;


    public AuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // da togliere la ||
        if (!request.getRequestURI().startsWith("/api") || request.getRequestURI().startsWith("/api")) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        // TEST
        /*
        if (header.equals("token")) {
            filterChain.doFilter(request, response);
            return;
        }
        */

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            String username = jwtUtil.extractUsername(token);


            User user = userRepository.findByUsername(username).orElse(null);
            Integer role = (user != null) ? user.getRole() : null;

            request.setAttribute("userRole", role);
            request.setAttribute("requesterUsername", username);
            request.setAttribute("requesterId", user.get_id());


            try {
                if (jwtUtil.isTokenExpired(token)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"message\": \"Unauthorized - Expired token\"}");
                    return;
                }


            } catch (Exception e) {
                // Invalid token
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"message\": \"Unauthorized - Invalid token\"}");
                return;
            }

        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Unauthorized - Missing token\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
