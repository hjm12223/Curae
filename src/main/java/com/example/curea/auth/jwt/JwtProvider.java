package com.example.curea.auth.jwt;

import static com.example.curea.auth.constant.JwtConstants.TOKEN_TYPE;

import com.example.curea.auth.enurmerated.TokenType;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;

    public String createAccessToken(UUID publicId) {
        return createToken(
            publicId,
            jwtProperties.getAccessTokenExpirationMillis()
        );
    }

    private String createToken(
        UUID publicId,
        Long expirationMillis
    ) {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
            .setSubject(publicId.toString())
            .setIssuedAt(now)
            .setExpiration(expiredDate)
            .claim(TOKEN_TYPE, TokenType.ACCESS_TOKEN.name())
            .signWith(jwtProperties.getSecretKey())
            .compact();
    }
}
