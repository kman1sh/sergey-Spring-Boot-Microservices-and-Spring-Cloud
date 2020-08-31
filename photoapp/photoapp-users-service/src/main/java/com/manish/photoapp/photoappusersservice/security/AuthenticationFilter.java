package com.manish.photoapp.photoappusersservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manish.photoapp.photoappusersservice.model.LoginRequestModel;
import com.manish.photoapp.photoappusersservice.service.UserService;
import com.manish.photoapp.photoappusersservice.shared.UserDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;


public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final UserService userService;

    private final Environment environment;

    public AuthenticationFilter(UserService userService, Environment environment) {
        this.userService = userService;
        this.environment = environment;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {


        try {
            LoginRequestModel credsFromRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequestModel.class);


            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            credsFromRequest.getEmail(),
                            credsFromRequest.getPassword(),
                            new ArrayList<>())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * after successful validation this method is called. Job of this method is to take user details (e.g email, userId)
     * and generate jwt token and then add this jwt token to "http response" header.
     * The client application then going to read this jwt token from "http response header" and use this jwt token in the subsequent
     * requests to our application as an "Authorization" header.
     * Task: read user details > use these details to generate jwt token (we will use userId for this)> add jwt token to response header
     * <p>
     * 1) read user details: from db, for that we need currently logged username, use Authentication @parameter which get created after successful login and have logged user username.
     */

    @Override
    protected void successfulAuthentication
    (HttpServletRequest request, HttpServletResponse response,
     FilterChain chain, Authentication authResult) throws IOException, ServletException {


        String username = ((User) authResult.getPrincipal()).getUsername();

        UserDto userDetails = userService.getUserDetailsByEmail(username);

        // jwt token generator syntax: required jsonwebtoken dependency
        String token = Jwts.builder().setSubject(userDetails.getUserId())
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(environment.getProperty("token.expiration.time"))))
                .signWith(SignatureAlgorithm.HS512, environment.getProperty("token.secrete"))
                .compact();

        //addHeader() will create a new header for response objection. in key and value pair.
        response.addHeader("token", token);
        response.addHeader("userId", userDetails.getUserId());
    }
}


