package com.horseracing.config;

import com.horseracing.security.JwtAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Auth endpoints - public
                .requestMatchers("/api/v1/auth/login", "/api/v1/auth/register",
                    "/api/v1/auth/refresh-token").permitAll()
                // Swagger - public
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/api-docs/**",
                    "/v3/api-docs/**").permitAll()
                // WebSocket - public (auth handled via STOMP)
                .requestMatchers("/ws/**").permitAll()
                // Static files - public
                .requestMatchers("/uploads/**").permitAll()
                // GET endpoints - public
                .requestMatchers(HttpMethod.GET, "/api/v1/races/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/seasons/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/horses/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/jockeys/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/rankings/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/dashboard/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/results/**").permitAll()
                // All other endpoints - require auth
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) -> {
                    res.setContentType("application/json;charset=UTF-8");
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.getWriter().write("{\"success\":false,\"message\":\"Unauthorized\"}");
                })
                .accessDeniedHandler((req, res, e) -> {
                    res.setContentType("application/json;charset=UTF-8");
                    res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    res.getWriter().write("{\"success\":false,\"message\":\"Forbidden\"}");
                })
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
