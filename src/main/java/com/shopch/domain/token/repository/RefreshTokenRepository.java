package com.shopch.domain.token.repository;

import com.shopch.domain.token.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByMember_Id(Long memberId);

    Optional<RefreshToken> findByToken(String token);

    void deleteByMember_Id(Long memberId);
}
