package com.product.CloudFileStorage.user.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.product.CloudFileStorage.user.security.jwt.JWTEntryPoint;
import com.product.CloudFileStorage.user.security.jwt.JWTFilter;
import com.product.CloudFileStorage.user.security.jwt.JWTUtil;
import com.product.CloudFileStorage.user.service.impl.UserDetailsServiceImpl;

import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JWTEntryPoint jwtEntryPoint;
    private final JWTUtil jwtUtil;

    // creates a new instance for the JWT Filter and validating JWT tokens
    @Bean
    public JWTFilter authenticationJwtTokenFilter() {
        return new JWTFilter(jwtUtil, userDetailsService);
    }

    // authenticates users
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                // authorizes request
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(
                                "/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs*/**",
                                "/swagger-ui.html")
                        .permitAll()
                        .anyRequest().authenticated())

                // enables basic auth
                .httpBasic(Customizer.withDefaults())

                // configures the session management to be stateless
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // configures jwt filter
                .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)

                // configures exception handling for jwt
                .exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(jwtEntryPoint))

                .authenticationManager(authenticationManager());

        return http.build();
    }

    // BCrypt password encoder used to encode passwords
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
