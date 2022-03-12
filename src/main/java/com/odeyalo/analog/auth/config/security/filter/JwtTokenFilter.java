package com.odeyalo.analog.auth.config.security.filter;

import com.odeyalo.analog.auth.config.security.jwt.utils.JwtTokenProvider;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenProvider provider;
    private final Logger LOGGER = LoggerFactory.getLogger(JwtTokenFilter.class);
    private final UserDetailsService userDetailsService;

    public JwtTokenFilter(JwtTokenProvider tokenProvider, @Qualifier("customUserDetailsService") UserDetailsService userDetailsService) {
        this.provider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwtToken = this.getJwtToken(request);
        String pathInfo = request.getServletPath();
        this.LOGGER.info("JWT TOKEN: {}, pathInfo: {}", jwtToken, pathInfo);
        try {
            if (jwtToken != null && this.provider.validateToken(jwtToken)) {
                String nickname = this.provider.getNicknameFromJwtToken(jwtToken);
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(nickname);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            this.LOGGER.info("CANNOT SET USER AUTH: {}", ex.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private String getJwtToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        return header.substring(7);
    }
}
