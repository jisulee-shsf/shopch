package com.app.domain.member.service;

import com.app.domain.member.entity.Member;
import com.app.domain.member.repository.MemberRepository;
import com.app.global.error.exception.AuthenticationException;
import com.app.global.error.exception.BusinessException;
import com.app.global.error.exception.EntityNotFoundException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import static com.app.domain.member.constant.MemberType.KAKAO;
import static com.app.domain.member.constant.Role.USER;
import static com.app.fixture.TimeFixture.FIXED_CLOCK;
import static com.app.fixture.TokenFixture.REFRESH_TOKEN_EXPIRATION_TIME;
import static com.app.global.error.ErrorType.*;
import static com.app.global.jwt.constant.TokenType.REFRESH;
import static com.app.global.util.DateTimeUtils.convertDateToLocalDateTime;
import static io.jsonwebtoken.Jwts.SIG.HS512;
import static io.jsonwebtoken.io.Decoders.BASE64URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.doReturn;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @MockitoSpyBean
    private Clock clock;

    @Value("${token.secret}")
    private String tokenSecret;

    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        secretKey = Keys.hmacShaKeyFor(BASE64URL.decode(tokenSecret));
    }

    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("회원을 등록한다.")
    @Test
    void registerMember() {
        // given
        Member member = createTestMember("member@email.com");

        // when
        Long memberId = memberService.registerMember(member);

        // then
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        assertThat(optionalMember)
                .isPresent()
                .get()
                .extracting("email")
                .isEqualTo(member.getEmail());
    }

    @DisplayName("이미 등록된 회원이 있을 때 회원과 같은 이메일로 등록을 시도할 경우, 예외가 발생한다.")
    @Test
    void registerMember_AlreadyRegisteredMember() {
        // given
        Member member = createTestMember("member@email.com");
        memberRepository.save(member);

        Member duplicateMember = createTestMember("member@email.com");

        // when & then
        assertThatThrownBy(() -> memberService.registerMember(duplicateMember))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ALREADY_REGISTERED_MEMBER.getErrorMessage());
    }

    @DisplayName("회원 아이디로 회원을 조회한다.")
    @Test
    void findMemberById() {
        // given
        Member member = createTestMember("member@email.com");
        memberRepository.save(member);

        Long memberId = member.getId();

        // when
        Member findMember = memberService.findMemberById(memberId);

        // then
        assertThat(findMember.getEmail()).isEqualTo(member.getEmail());
    }

    @DisplayName("등록된 회원이 없을 때 조회를 시도할 경우, 예외가 발생한다.")
    @Test
    void findMemberById_MemberNotFound() {
        // given
        Long memberId = 1L;

        // when & then
        assertThatThrownBy(() -> memberService.findMemberById(memberId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(MEMBER_NOT_FOUND.getErrorMessage());
    }

    @DisplayName("이메일로 회원을 조회한다.")
    @Test
    void findMemberByEmail() {
        // given
        Member member = createTestMember("member@email.com");
        memberRepository.save(member);

        String email = member.getEmail();

        // when
        Optional<Member> optionalMember = memberService.findMemberByEmail(email);

        // then
        assertThat(optionalMember)
                .isPresent()
                .get()
                .extracting("email")
                .isEqualTo(email);
    }

    @DisplayName("리프레시 토큰으로 회원을 조회한다.")
    @Test
    void findMemberByRefreshToken() {
        // given
        doReturn(FIXED_CLOCK.instant()).when(clock).instant();

        Date issueDate = Date.from(clock.instant());
        Date refreshTokenExpirationDate = Date.from(issueDate.toInstant().plusMillis(REFRESH_TOKEN_EXPIRATION_TIME));
        Member member = createTestMemberWithRefreshToken(issueDate, refreshTokenExpirationDate);
        memberRepository.save(member);

        String refreshToken = member.getRefreshToken();

        // when
        Member findMember = memberService.findMemberByRefreshToken(refreshToken);

        // then
        assertThat(findMember)
                .extracting("refreshToken", "refreshTokenExpirationDateTime")
                .containsExactly(refreshToken, member.getRefreshTokenExpirationDateTime());
    }

    @DisplayName("리프레시 토큰을 가진 회원이 없을 때 조회를 시도할 경우, 예외가 발생한다.")
    @Test
    void findMemberByRefreshToken_MemberNotFound() {
        // given
        doReturn(FIXED_CLOCK.instant()).when(clock).instant();

        Date issueDate = Date.from(clock.instant());
        Date refreshTokenExpirationDate = Date.from(issueDate.toInstant().plusMillis(REFRESH_TOKEN_EXPIRATION_TIME));
        String refreshToken = createTestRefreshToken(issueDate, refreshTokenExpirationDate);

        // when & then
        assertThatThrownBy(() -> memberService.findMemberByRefreshToken(refreshToken))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(MEMBER_NOT_FOUND.getErrorMessage());
    }

    @DisplayName("조회한 회원의 리프레시 토큰이 만료된 경우, 예외가 발생한다.")
    @Test
    void findMemberByRefreshToken_ExpiredRefreshToken() {
        // given
        doReturn(FIXED_CLOCK.instant()).when(clock).instant();

        Date issueDate = Date.from(clock.instant().minusMillis(REFRESH_TOKEN_EXPIRATION_TIME + 1000));
        Date refreshTokenExpirationDate = Date.from(issueDate.toInstant().plusMillis(REFRESH_TOKEN_EXPIRATION_TIME));
        Member member = createTestMemberWithRefreshToken(issueDate, refreshTokenExpirationDate);
        memberRepository.save(member);

        String expiredRefreshToken = member.getRefreshToken();

        // when & then
        assertThat(member.getRefreshTokenExpirationDateTime()).isBefore(LocalDateTime.now(clock));
        assertThatThrownBy(() -> memberService.findMemberByRefreshToken(expiredRefreshToken))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage(EXPIRED_REFRESH_TOKEN.getErrorMessage());
    }

    private Member createTestMember(String email) {
        return Member.builder()
                .name("member")
                .email(email)
                .role(USER)
                .profile("profile")
                .memberType(KAKAO)
                .build();
    }

    private String createTestRefreshToken(Date issueDate, Date expirationDate) {
        return Jwts.builder()
                .subject(REFRESH.name())
                .issuedAt(issueDate)
                .expiration(expirationDate)
                .signWith(secretKey, HS512)
                .compact();
    }

    private Member createTestMemberWithRefreshToken(Date issueDate, Date refreshTokenExpirationDate) {
        Member member = createTestMember("member@email.com");
        String refreshToken = createTestRefreshToken(issueDate, refreshTokenExpirationDate);
        LocalDateTime refreshTokenExpirationDateTime = convertDateToLocalDateTime(refreshTokenExpirationDate);

        return member.toBuilder()
                .refreshToken(refreshToken)
                .refreshTokenExpirationDateTime(refreshTokenExpirationDateTime)
                .build();
    }
}
