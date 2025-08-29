package com.app.domain.member.repository;

import com.app.domain.member.constant.OAuthProvider;
import com.app.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByOauthIdAndOauthProvider(String oauthId, OAuthProvider oauthProvider);
}
