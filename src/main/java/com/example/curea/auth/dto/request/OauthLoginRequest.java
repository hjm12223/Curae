package com.example.curea.auth.dto.request;

import com.example.curea.User.Entity.enurmurate.SocialProvider;

public record OauthLoginRequest(
    String code,
    SocialProvider socialProvider
) {

}
