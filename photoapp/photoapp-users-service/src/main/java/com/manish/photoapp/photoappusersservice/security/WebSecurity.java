package com.manish.photoapp.photoappusersservice.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    @Autowired
    private Environment env;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
//        http.authorizeRequests().antMatchers("/**").hasIpAddress(env.getProperty("gateway.ip"));
        http.authorizeRequests().antMatchers("/**").permitAll();

        http.headers().frameOptions().disable(); //to allow h2-console run as it uses frame to display dahsboard.
    }
}
