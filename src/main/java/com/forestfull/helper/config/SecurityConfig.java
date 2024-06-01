package com.forestfull.helper.config;

import com.forestfull.helper.controller.ClientController;
import com.forestfull.helper.controller.ManagementController;
import com.forestfull.helper.service.CommmonFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.util.AntPathMatcher;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    private final CommmonFilter commmonFilter;

    public static final String[] ignoringPattern = {"/aXQgaXMgZ3**", "/favicon.**", "/script/**", "/style/**", "/font/**"};
    public static final String[] clientUriPatterns = Arrays.stream(ClientController.URI.class.getFields())
            .map(field -> {
                try {
                    return field.get(ClientController.URI.class.getFields());
                } catch (IllegalAccessException e) {
                    e.printStackTrace(System.out);
                    log.error(e.getMessage());
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .map(uri -> uri + "/**")
            .toArray(String[]::new);
    public static final String[] managementUriPatterns = Arrays.stream(ManagementController.URI.class.getFields())
            .map(field -> {
                try {
                    return field.get(ManagementController.URI.class.getFields());
                } catch (IllegalAccessException e) {
                    e.printStackTrace(System.out);
                    log.error(e.getMessage());
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .map(uri -> uri + "/**")
            .toArray(String[]::new);

    public static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(reg -> reg.requestMatchers(HttpMethod.GET, ignoringPattern).permitAll())
                .authorizeHttpRequests(reg -> reg.requestMatchers(clientUriPatterns)
                        .access((auth, ctx) -> {
                            final Optional<String> client = Optional.ofNullable(ctx.getRequest().getHeader("client"));
                            return new AuthorizationDecision(client.isPresent());
                        }))
                .authorizeHttpRequests(reg -> reg.requestMatchers(managementUriPatterns).authenticated())
                .httpBasic(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterAfter(commmonFilter, HeaderWriterFilter.class)
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        return new InMemoryUserDetailsManager(User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .roles("ADMIN")
                .build());
    }
}