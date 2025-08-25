package com.example.curea.auth.jwt;

import static com.example.curea.auth.constant.JwtConstants.AUTHORIZATION_HEADER;
import static com.example.curea.auth.constant.JwtConstants.BEARER_PREFIX;
import static com.example.curea.auth.constant.JwtConstants.BEARER_PREFIX_LENGTH;
import static com.example.curea.auth.constant.JwtConstants.REQUEST_ATTR_PUBLIC_ID;

import com.example.curea.common.error.ErrorCode;
import com.example.curea.common.exception.AuthException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtResolver jwtResolver;

    @Override
    public boolean preHandle(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull Object handler
    ) throws Exception {

        String token = extractToken(request);

        if (token == null) {
            return true;
        }

        if (!jwtResolver.isValid(token)) {
            throw new AuthException(ErrorCode.AUTH_TOKEN_INVALID);
        }
        setUserAttributes(request, token);
        return true;
    }

    private void setUserAttributes(HttpServletRequest request, String token) {
        UUID publicId = jwtResolver.extractPublicId(token);
        request.setAttribute(REQUEST_ATTR_PUBLIC_ID, publicId);

    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            return null;
        }
        return header.substring(BEARER_PREFIX_LENGTH);
    }


}
