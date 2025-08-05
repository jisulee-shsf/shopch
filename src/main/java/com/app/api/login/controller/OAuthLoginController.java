package com.app.api.login.controller;

import com.app.api.login.controller.dto.request.OAuthLoginRequest;
import com.app.api.login.service.OAuthLoginService;
import com.app.api.login.service.dto.response.OAuthLoginResponse;
import com.app.global.jwt.service.TokenExtractor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequiredArgsConstructor
public class OAuthLoginController {

    private final OAuthLoginService oauthLoginService;
    private final TokenExtractor tokenExtractor;

    @PostMapping("/api/oauth/login")
    public ResponseEntity<OAuthLoginResponse> oauthLogin(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                         @Valid @RequestBody OAuthLoginRequest oauthLoginRequest) {
        String accessToken = tokenExtractor.extractToken(authorizationHeader);
        Date issueDate = new Date();
        return ResponseEntity.ok(oauthLoginService.oauthLogin(oauthLoginRequest.toServiceRequest(), accessToken, issueDate));
    }
}
