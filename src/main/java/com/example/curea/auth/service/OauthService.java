package com.example.curea.auth.service;

import com.example.curea.User.Entity.User;
import com.example.curea.User.repository.UserRepository;
import com.example.curea.auth.dto.request.OauthLoginRequest;
import com.example.curea.auth.dto.response.AuthTokenDto;
import com.example.curea.auth.entity.Accessor;
import com.example.curea.auth.jwt.JwtProvider;
import com.example.curea.auth.provider.OauthProvider;
import com.example.curea.auth.provider.OauthProviders;
import com.example.curea.auth.userinfo.OauthUserInfo;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OauthService {

    private final OauthProviders oauthProviders;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public AuthTokenDto login(final OauthLoginRequest request) {
        final OauthProvider provider = oauthProviders.getProvider(request.socialProvider());
        final String accessToken = provider.getAccessToken(request.code());
        final OauthUserInfo userInfo = provider.getUserInfo(accessToken);
        final User user = userRepository.findBySocialId(userInfo.getSocialId())
            .orElseGet(() -> userRepository.save(
                    User.signIn(
                        userInfo.getEmail(),
                        userInfo.getName(),
                        userInfo.getSocialId(),
                        provider.getProvider()
                    )
                )
            );
        return new AuthTokenDto(jwtProvider.createAccessToken(user.getPublicId()));
    }

    public Accessor getCurrentAccessor(UUID publicId) {
        return Accessor.user(publicId);
    }

}
