package com.example.curea.auth.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JwtConstants {

    public static final String TOKEN_TYPE = "token_type";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();

    public static final String REQUEST_ATTR_PUBLIC_ID = "publicId";
}
