package com.manish.photoapp.photoappzuulapigateway.security;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
/**
 * when user try to access a protected web microservice that required authorization, user will need to include a valid
 * JWT token in the authorization header.
 * So here we have create a authorization filter, a filter that will validate token provided in authorization header.
 * if token valid, zuul api let the request pass through and if not valid, zuul gateway will not let the request pass thru it.
 *
 */
public class AuthorizationFilter extends BasicAuthenticationFilter {

    private Environment environment;

    public AuthorizationFilter(AuthenticationManager authenticationManager, Environment environment) {
        super(authenticationManager);
        this.environment = environment;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        //get "Authorization" Header from req to see if it contains Authorization token

        String authorizationHeader = request.getHeader(environment.getProperty("authorization.token.header.name"));

        //if null or not valid type pass to next filter chain without doing anything and return.
        if(authorizationHeader == null || !authorizationHeader.startsWith(environment.getProperty("authorization.token.header.prefix"))) {
            chain.doFilter(request,response);
            return;
        }

        // if we are here means we have authorization token, lets validate it.
        UsernamePasswordAuthenticationToken authentication =  getAuthentication(request);

        // https://javarevisited.blogspot.com/2018/02/what-is-securitycontext-and-SecurityContextHolder-Spring-security.html
        // The SecurityContext is used to store the details of the currently authenticated user, also known as a principle.
        // So, if you have to get the username or any other user details, you need to get this SecurityContext first.
        // The SecurityContextHolder is a helper class, which provides access to the security context.
        //
        // In order to get the current username, you first need a SecurityContext, which is obtained
        // from SecurityContextHolder. This SecurityContext keep the user details in an "Authentication" object,
        // which can be obtained by calling getAuthentication() method. or which can be "set" by setAuthentication() method.
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    // why we are returning this class object?
    // Actually we want instance of "Authentication" object. Authentication is an interface and UsernamePasswordAuthenticationToken implement it.
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(environment.getProperty("authorization.token.header.name"));

        if(authorizationHeader == null) {
            return  null;
        }
        // stripping off Bearer part of token: NOTE: DONT FORGET TO REPLACE DASH.
        String token = authorizationHeader.replace(environment.getProperty("authorization.token.header.prefix") +" ", "");

        //fetching stored Subject(userId) on the token. if fetched then token is valid else token is tampered or not created by the server.
        String userId = Jwts.parser()
                .setSigningKey(environment.getProperty("token.secret"))
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

        if(userId == null) {
            return null;
        }

        // we here means get successfully got the subject from jwt token. return  UsernamePasswordAuthenticationToken() object with SUBJECT as Principal.
        // "principal" is an object (can be generic or specific class type) which store SOME info about currently logged user.
        return new UsernamePasswordAuthenticationToken(userId,null, new ArrayList<>());
    }
}
