package com.shopch.domain.token.service;

import com.shopch.domain.token.entity.RefreshToken;
import com.shopch.domain.token.repository.RefreshTokenRepository;
import com.shopch.global.error.ErrorCode;
import com.shopch.global.error.exception.AuthenticationException;
import com.shopch.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final Clock clock;

    @Transactional
    public void registerRefreshToken(RefreshToken refreshToken) {
        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void deleteRefreshToken(Long memberId) {
        refreshTokenRepository.deleteByMember_Id(memberId);
    }

    public Optional<RefreshToken> findRefreshToken(Long memberId) {
        return refreshTokenRepository.findByMember_Id(memberId);
    }

    public RefreshToken getRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        validateRefreshTokenExpiration(refreshToken, LocalDateTime.now(clock));
        return refreshToken;
    }

    private void validateRefreshTokenExpiration(RefreshToken refreshToken, LocalDateTime now) {
        if (refreshToken.isExpired(now)) {
            throw new AuthenticationException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }
    }
}
