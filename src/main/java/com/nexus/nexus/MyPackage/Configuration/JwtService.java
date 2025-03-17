package com.nexus.nexus.MyPackage.Configuration;

import java.io.IOException;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nexus.nexus.MyPackage.Entities.UserModal;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtService extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final JwtRequestUtil jwtRequestUtil;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String header = request.getHeader("Authorization");
            String username = null;
            String jwtToken = null;
            if (header != null && header.startsWith("Bearer ")) {
                jwtToken = header.substring(7);
                username = jwtRequestUtil.extractUsername(jwtToken);
            }
            if (username != null && jwtToken != null
                    && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserModal userDetails = (UserModal) userDetailsService.loadUserByUsername(username);
                if (jwtRequestUtil.validateToken(jwtToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
                            null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException e) {
            // Only JWT-related exceptions are handled here.
            jwtAuthenticationEntryPoint.commence(request, response,
                    new BadCredentialsException(e.getMessage(), e));
        } catch (Exception e) {
            // For other exceptions, let them propagate so they are handled by the global
            // error handling (e.g., 404 for unmapped URLs).
            throw e;
        }
    }
}
