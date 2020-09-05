package com.manish.photoapp.photoappzuulapigateway.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private final Environment env;

    @Autowired
    public WebSecurity(Environment env) {
        this.env = env;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.headers().frameOptions().disable();
        http.authorizeRequests()
                .antMatchers(env.getProperty("api.h2console.url.path")).permitAll()
                .antMatchers(HttpMethod.POST, env.getProperty("api.login.url.path")).permitAll()
                .antMatchers(HttpMethod.POST, env.getProperty("api.registration.url.path")).permitAll()
                .anyRequest().authenticated()
        .and()
        .addFilter(new AuthorizationFilter(authenticationManager(), env));


        // when client app starts communication with server side app, there is something called session is created
        // this session uniquely identify which client application is communicating with server.
        // this session and cookies cache some info in client browser, so this can make Authorization header(jwt token) also cached
        // so in subsequent request even if token is not provided in header it will be stored in cached and request will get authorized
        // we want this. Each request should be authorized from header and no cache of token should be maintain.
        // Hence, this below config makes our app stateless:  Spring Security will never create an {@link HttpSession} and it will never use it
        // to obtain the {@link SecurityContext}
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}
