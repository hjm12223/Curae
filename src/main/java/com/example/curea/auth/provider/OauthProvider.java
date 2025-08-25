package com.example.curea.auth.provider;

import com.example.curea.User.Entity.enurmurate.SocialProvider;
import com.example.curea.auth.userinfo.OauthUserInfo;

public interface OauthProvider {

    SocialProvider getProvider();

    String getAccessToken(String code);

    OauthUserInfo getUserInfo(String accessToken);

}
