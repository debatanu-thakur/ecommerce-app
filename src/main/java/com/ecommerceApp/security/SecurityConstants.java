package com.ecommerceApp.security;

public class SecurityConstants {
    public static final String SECRET = "SecretKeyToGenJWTsYo";
    public static final long EXPIRATION_TIME = 864_000_00; // 1 day
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/api/user/create";
    public static final String ITEMS_URL = "/api/item/**";
}
