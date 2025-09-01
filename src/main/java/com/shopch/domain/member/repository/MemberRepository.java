package com.shopch.domain.member.repository;

import com.shopch.external.oauth.constant.OAuthProvider;
import com.shopch.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByOauthIdAndOauthProvider(String oauthId, OAuthProvider oauthProvider);
}
