package com.app.domain.member.entity;

import com.app.domain.order.entity.Order;
import com.app.domain.orderProduct.entity.OrderProduct;
import com.app.domain.product.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.app.domain.member.constant.MemberType.KAKAO;
import static com.app.domain.member.constant.Role.USER;
import static com.app.domain.order.constant.OrderStatus.INIT;
import static com.app.domain.product.constant.ProductSellingStatus.SELLING;
import static com.app.domain.product.constant.ProductType.PRODUCT_A;
import static com.app.fixture.TimeFixture.FIXED_INSTANT;
import static com.app.fixture.TimeFixture.FIXED_TIME_ZONE;
import static com.app.fixture.TokenFixture.REFRESH_TOKEN_EXPIRATION_TIME;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.assertj.core.api.Assertions.assertThat;

class MemberTest {

    @DisplayName("회원 생성 시 리프레시 토큰과 리프레시 토큰 만료 일시는 null이다.")
    @Test
    void create() {
        // given
        // when
        Member member = Member.create("member", "member@email.com", USER, "profile", KAKAO);

        // then
        assertThat(member.getRefreshToken()).isNull();
        assertThat(member.getRefreshTokenExpirationDateTime()).isNull();
    }

    @DisplayName("회원의 리프레시 토큰과 리프레시 토큰 만료 일시를 반영한다.")
    @Test
    void updateRefreshToken() {
        // given
        Member member = createTestMember();
        LocalDateTime issueDateTime = LocalDateTime.ofInstant(FIXED_INSTANT, FIXED_TIME_ZONE);
        LocalDateTime refreshTokenExpirationDateTime = issueDateTime.plus(REFRESH_TOKEN_EXPIRATION_TIME, MILLIS);

        // when
        member.updateRefreshToken("refresh-token", refreshTokenExpirationDateTime);

        // then
        assertThat(member.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(member.getRefreshTokenExpirationDateTime()).isEqualTo(refreshTokenExpirationDateTime);
    }

    @DisplayName("회원의 리프레시 토큰 만료 일시를 현재 일시로 변경해 만료한다.")
    @Test
    void expireRefreshToken() {
        // given
        LocalDateTime issueDateTime = LocalDateTime.ofInstant(FIXED_INSTANT, FIXED_TIME_ZONE);
        LocalDateTime refreshTokenExpirationDateTime = issueDateTime.plus(REFRESH_TOKEN_EXPIRATION_TIME, MILLIS);
        Member member = createTestMemberWithRefreshToken("refresh-token", refreshTokenExpirationDateTime);

        LocalDateTime now = LocalDateTime.ofInstant(FIXED_INSTANT, FIXED_TIME_ZONE);

        // when
        member.expireRefreshToken(now);

        // then
        assertThat(member.getRefreshTokenExpirationDateTime()).isEqualTo(now);
    }

    @DisplayName("회원 아이디와 주문한 회원 아이디가 같은지 비교한다.")
    @Test
    void isSameId() {
        // given
        Member member = createTestMember();
        ReflectionTestUtils.setField(member, "id", 1L);

        LocalDateTime orderDateTime = LocalDateTime.ofInstant(FIXED_INSTANT, FIXED_TIME_ZONE);
        Order order = createTestOrder(member, orderDateTime);

        // when
        boolean result = member.isSameId(order.getMember().getId());

        // then
        assertThat(result).isTrue();
    }

    private Member createTestMember() {
        return createTestMemberWithRefreshToken(null, null);
    }

    private Member createTestMemberWithRefreshToken(String refreshToken, LocalDateTime refreshTokenExpirationDateTime) {
        return Member.builder()
                .name("member")
                .email("member@email.com")
                .role(USER)
                .profile("profile")
                .memberType(KAKAO)
                .refreshToken(refreshToken)
                .refreshTokenExpirationDateTime(refreshTokenExpirationDateTime)
                .build();
    }

    private Order createTestOrder(Member member, LocalDateTime orderDateTime) {
        Product product = Product.builder()
                .name("product")
                .productType(PRODUCT_A)
                .productSellingStatus(SELLING)
                .price(10000)
                .stockQuantity(1)
                .build();

        OrderProduct orderProduct = OrderProduct.builder()
                .product(product)
                .orderQuantity(1)
                .build();

        return Order.builder()
                .member(member)
                .orderDateTime(orderDateTime)
                .orderStatus(INIT)
                .orderProducts(List.of(orderProduct))
                .build();
    }
}
