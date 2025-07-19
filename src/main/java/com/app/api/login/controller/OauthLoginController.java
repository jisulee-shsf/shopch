package com.app.api.login.controller;

import com.app.api.login.controller.dto.request.OauthLoginRequest;
import com.app.api.login.service.OauthLoginService;
import com.app.api.login.service.dto.response.OauthLoginResponse;
import com.app.global.util.AuthorizationHeaderUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequiredArgsConstructor
public class OauthLoginController {

    private final OauthLoginService oauthLoginService;

    @PostMapping("/api/oauth/login")
    public ResponseEntity<OauthLoginResponse> oauthLogin(HttpServletRequest httpServletRequest,
                                                         @Valid @RequestBody OauthLoginRequest oauthLoginRequest) {
        String authorizationHeader = AuthorizationHeaderUtils.getAuthorizationHeader(httpServletRequest);
        AuthorizationHeaderUtils.validateAuthorizationHeader(authorizationHeader);

        String accessToken = authorizationHeader.split(" ")[1];
        return ResponseEntity.ok(oauthLoginService.oauthLogin(oauthLoginRequest.toServiceRequest(), accessToken, new Date()));
    }
}
