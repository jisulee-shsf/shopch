package com.app.api.logout.service;

import com.app.domain.token.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LogoutService {

    private final RefreshTokenService refreshTokenService;

    public void logout(Long memberId) {
        refreshTokenService.deleteRefreshToken(memberId);
    }
}
