package com.tony.meeting.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers("/", "/account", "/account/login", "/account/sign-up",
                        "/account/check-email", "/account/check-email-token",
                        "/account/email-login", "/account/check-email-login",
                        "/account/login-link").permitAll()
                .mvcMatchers(HttpMethod.GET, "/account/profile/*").permitAll()
                .anyRequest().authenticated();
    }
}
