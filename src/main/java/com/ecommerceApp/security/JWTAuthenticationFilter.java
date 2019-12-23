package com.ecommerceApp.security;

import com.auth0.jwt.JWT;
import com.ecommerceApp.controllers.UserController;
import com.ecommerceApp.model.persistence.User;
import com.ecommerceApp.model.requests.CreateUserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;



import static com.ecommerceApp.security.SecurityConstants.*;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private static final Logger log = LoggerFactory.getLogger(JWTAuthenticationFilter.class);
    private AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
            CreateUserRequest creds = new ObjectMapper()
                    .readValue(req.getInputStream(), CreateUserRequest.class);
            log.trace("AuthenticationSuccess", 1);
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getUsername(),
                            creds.getPassword(),
                            new ArrayList<>())
            );
        } catch (IOException e) {
            log.error("Error authentication user");
            log.warn(e.getMessage());
            log.trace("AuthenticationFailure", 1);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {

        String token = "";
        try {
            token = JWT.create()
                    .withSubject(((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername())
                    .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .sign(HMAC512(SECRET.getBytes()));
        } catch (Exception ex) {
            log.error("Error creating jwt token");
            log.warn(ex.getMessage());
        }
        res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
    }

}
