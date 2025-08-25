package com.example.curea.auth.provider.impl;

import static com.example.curea.auth.constant.JwtConstants.BEARER_PREFIX;
import static com.example.curea.auth.constant.KakaoOauthConstants.CLIENT_ID_KEY;
import static com.example.curea.auth.constant.KakaoOauthConstants.CLIENT_SECRET_KEY;
import static com.example.curea.auth.constant.KakaoOauthConstants.CODE_KEY;
import static com.example.curea.auth.constant.KakaoOauthConstants.GRANT_TYPE;
import static com.example.curea.auth.constant.KakaoOauthConstants.GRANT_TYPE_KEY;
import static com.example.curea.auth.constant.KakaoOauthConstants.REDIRECT_URI_KEY;
import static com.example.curea.common.error.ErrorCode.OAUTH_TOKEN_REQUEST_FAILED;
import static com.example.curea.common.error.ErrorCode.OAUTH_USERINFO_RESPONSE_EMPTY;

import com.example.curea.User.Entity.enurmurate.SocialProvider;
import com.example.curea.auth.dto.response.KakaoAccessTokenResponse;
import com.example.curea.auth.dto.response.KakaoUserInfoResponse;
import com.example.curea.auth.provider.OauthProvider;
import com.example.curea.auth.userinfo.KakaoUserInfo;
import com.example.curea.auth.userinfo.OauthUserInfo;
import com.example.curea.common.exception.AuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class KakaoProvider implements OauthProvider {

    private final WebClient webClient;
    private final String clientId;
    private final String redirectUri;
    private final String tokenUri;
    private final String userInfoUri;
    private final String clientSecret;

    public KakaoProvider(
        WebClient webClient,
        @Value("${oauth.kakao.client-id}") String clientId,
        @Value("${oauth.kakao.redirect-uri}") String redirectUri,
        @Value("${oauth.kakao.token-uri}") String tokenUri,
        @Value("${oauth.kakao.user-info-uri}") String userInfoUri,
        @Value("${oauth.kakao.secret}") String clientSecret) {
        this.webClient = webClient;
        this.clientId = clientId;
        this.redirectUri = redirectUri;
        this.tokenUri = tokenUri;
        this.clientSecret = clientSecret;
        this.userInfoUri = userInfoUri;
    }

    @Override
    public SocialProvider getProvider() {
        return SocialProvider.KAKAO;
    }

    @Override
    public String getAccessToken(final String code) {
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add(GRANT_TYPE_KEY, GRANT_TYPE);
        data.add(CLIENT_ID_KEY, clientId);
        data.add(REDIRECT_URI_KEY, redirectUri);
        data.add(CLIENT_SECRET_KEY, clientSecret);
        data.add(CODE_KEY, code);
        return webClient.post()
            .uri(tokenUri)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(BodyInserters.fromFormData(data))
            .retrieve()
            .onStatus(
                HttpStatusCode::isError,
                this::handleOauthError
            )
            .bodyToMono(KakaoAccessTokenResponse.class)
            .map(KakaoAccessTokenResponse::accessToken)
            .block();
    }

    private Mono<? extends Throwable> handleOauthError(final ClientResponse response) {
        return response.bodyToMono(String.class)
            .flatMap(errorBody ->
                Mono.error(new AuthException(OAUTH_TOKEN_REQUEST_FAILED))
            );
    }

    @Override
    public OauthUserInfo getUserInfo(String accessToken) {
        KakaoUserInfoResponse response = fetchUserInfo(accessToken);
        return new KakaoUserInfo(response);
    }

    private KakaoUserInfoResponse fetchUserInfo(String accessToken) {
        return webClient
            .post()
            .uri(userInfoUri)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + accessToken)
            .retrieve()
            .onStatus(
                HttpStatusCode::isError,
                this::handleOauthError
            )
            .bodyToMono(KakaoUserInfoResponse.class)
            .blockOptional()
            .orElseThrow(() -> new AuthException(OAUTH_USERINFO_RESPONSE_EMPTY));
    }

}
