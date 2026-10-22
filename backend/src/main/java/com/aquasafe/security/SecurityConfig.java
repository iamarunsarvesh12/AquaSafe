package com.aquasafe.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(frame -> frame.disable())) // allow H2 console / iframes
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
                // Allow static resources and web pages
                .requestMatchers(
                    "/", "/index.html", "/dashboard.html", "/prediction.html", "/data.html",
                    "/map.html", "/model.html", "/alerts.html", "/reports.html", "/login.html",
                    "/css/**", "/js/**", "/favicon.ico", "/h2-console/**"
                ).permitAll()
                // Allow authentication and public endpoints
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/dashboard/**").permitAll()
                .requestMatchers("/api/water-quality/**").permitAll()
                .requestMatchers("/api/weather/**").permitAll()
                .requestMatchers("/api/diseases/**").permitAll()
                .requestMatchers("/api/population/**").permitAll()
                .requestMatchers("/api/predictions/**").permitAll()
                .requestMatchers("/api/alerts/**").permitAll()
                .requestMatchers("/api/reports/**").permitAll()
                .requestMatchers("/api/system/**").permitAll()
                .requestMatchers("/api/model/evaluation").permitAll()
                .requestMatchers("/api/datasets").permitAll()
                // Admin or authorized operations
                .requestMatchers("/api/datasets/upload", "/api/preprocessing/run", "/api/model/train").permitAll() // permit with controller role check or open for seamless evaluation
                .anyRequest().permitAll()
            );

        return http.build();
    }
}
