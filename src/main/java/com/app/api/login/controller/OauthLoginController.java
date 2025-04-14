package com.app.api.login.controller;

import com.app.api.login.dto.OauthLoginRequest;
import com.app.api.login.dto.OauthLoginResponse;
import com.app.api.login.service.OauthLoginService;
import com.app.api.login.validator.OauthValidator;
import com.app.domain.member.constant.MemberType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

import static com.app.global.util.AuthorizationHeaderUtils.validateAuthorizationHeader;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OauthLoginController {

    private final OauthValidator oauthValidator;
    private final OauthLoginService oauthLoginService;

    @PostMapping("/oauth/login")
    public ResponseEntity<OauthLoginResponse> oauthLogin(HttpServletRequest httpServletRequest,
                                                         @Valid @RequestBody OauthLoginRequest oauthLoginRequest) {
        String authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION);
        validateAuthorizationHeader(authorizationHeader);

        String memberType = oauthLoginRequest.getMemberType();
        oauthValidator.validateMemberType(memberType);

        String accessToken = authorizationHeader.split(" ")[1];
        Date issueDate = new Date();

        return ResponseEntity.ok(oauthLoginService.oauthLogin(MemberType.from(memberType), accessToken, issueDate));
    }
}
