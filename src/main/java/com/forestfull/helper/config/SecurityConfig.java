package com.forestfull.helper.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
  	public WebSecurityCustomizer webSecurityCustomizer() {
  		return (web) -> web.ignoring().requestMatchers("favicon.ico","/resources/**");
  	}

  	@Bean
  	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
  		return http.build();
  	}

  	@Bean
  	public UserDetailsService userDetailsService() {
  		UserDetails user = User.withDefaultPasswordEncoder()
  			.username("user")
  			.password("password")
  			.roles("USER")
  			.build();
  		UserDetails admin = User.withDefaultPasswordEncoder()
  			.username("admin")
  			.password("password")
  			.roles("ADMIN", "USER")
  			.build();
  		return new InMemoryUserDetailsManager(user, admin);
  	}

}
