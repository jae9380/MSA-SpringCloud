package com.example.userservice.security;

import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurity {

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Environment env;

    @Bean
    protected SecurityFilterChain config(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);

        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        http.csrf( (csrf) -> csrf.disable());

        http.authorizeHttpRequests((authz) -> authz
                                .requestMatchers(new AntPathRequestMatcher("/actuator/**")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/users", "POST")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/welcome")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/health-check")).permitAll()
                                .requestMatchers("/**").access(
                                        new WebExpressionAuthorizationManager("hasIpAddress('127.0.0.1') or hasIpAddress('192.168.55.187')")) // host pc ip address
                                .anyRequest().authenticated()
                )
                .authenticationManager(authenticationManager)
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilter(getAuthenticationFilter(authenticationManager));

        return http.build();
    }

    public AuthenticationManager authenticationManager(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
        return auth.build();
    }

    private AuthenticationFilter getAuthenticationFilter(AuthenticationManager authenticationManager) throws Exception {
        return new AuthenticationFilter(authenticationManager, userService, env);
    }

}