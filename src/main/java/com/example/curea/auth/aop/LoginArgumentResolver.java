package com.example.curea.auth.aop;


import static com.example.curea.auth.constant.JwtConstants.REQUEST_ATTR_PUBLIC_ID;

import com.example.curea.auth.entity.Accessor;
import com.example.curea.auth.service.OauthService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class LoginArgumentResolver implements HandlerMethodArgumentResolver {

    private final OauthService oauthService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Auth.class)
            && parameter.getParameterType().equals(Accessor.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
        ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        UUID publicId = (UUID) request.getAttribute(REQUEST_ATTR_PUBLIC_ID);

        return oauthService.getCurrentAccessor(publicId);
    }
}
