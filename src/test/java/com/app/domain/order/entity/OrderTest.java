package com.app.domain.order.entity;

import com.app.domain.member.entity.Member;
import com.app.domain.orderProduct.entity.OrderProduct;
import com.app.domain.product.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.app.domain.member.constant.MemberType.KAKAO;
import static com.app.domain.member.constant.Role.USER;
import static com.app.domain.order.constant.OrderStatus.INIT;
import static com.app.domain.product.constant.ProductSellingStatus.SELLING;
import static com.app.domain.product.constant.ProductType.PRODUCT_A;
import static com.app.fixture.TimeFixture.FIXED_INSTANT;
import static com.app.fixture.TimeFixture.FIXED_TIME_ZONE;
import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    @DisplayName("주문 생성 시 주문 상태는 INIT이다.")
    @Test
    void create() {
        // given
        Member member = createTestMember();
        LocalDateTime orderDateTime = LocalDateTime.ofInstant(FIXED_INSTANT, FIXED_TIME_ZONE);

        Product product = createTestProduct("product", 10000, 1);
        OrderProduct orderProduct = createTestOrderProduct(product, 1);

        // when
        Order order = Order.create(member, orderDateTime, List.of(orderProduct));

        // then
        assertThat(order.getOrderStatus()).isEqualTo(INIT);
    }

    @DisplayName("주문 생성 시 주문이 회원을 참조하는 단방향 연관관계가 설정된.")
    @Test
    void create_UnidirectionalRelationship() {
        // given
        Member member = createTestMember();
        LocalDateTime orderDateTime = LocalDateTime.ofInstant(FIXED_INSTANT, FIXED_TIME_ZONE);

        Product product = createTestProduct("product", 10000, 1);
        OrderProduct orderProduct = createTestOrderProduct(product, 1);

        // when
        Order order = createTestOrder(member, orderDateTime, List.of(orderProduct));

        // then
        assertThat(order.getMember()).isEqualTo(member);
    }

    @DisplayName("주문 생성 시 주문과 주문 상품이 서로 참조하는 양방향 연관관계가 설정된다.")
    @Test
    void create_BidirectionalRelationship() {
        // given
        Member member = createTestMember();
        LocalDateTime orderDateTime = LocalDateTime.ofInstant(FIXED_INSTANT, FIXED_TIME_ZONE);

        Product product = createTestProduct("product", 10000, 1);
        OrderProduct orderProduct = createTestOrderProduct(product, 1);

        // when
        Order order = createTestOrder(member, orderDateTime, List.of(orderProduct));

        // then
        assertThat(order.getOrderProducts().contains(orderProduct)).isTrue();
        assertThat(orderProduct.getOrder()).isEqualTo(order);
    }

    @DisplayName("주문 생성 시 총 주문 금액은 주문 상품 금액의 합계이다.")
    @Test
    void create_getTotalPrice() {
        // given
        Member member = createTestMember();
        LocalDateTime orderDateTime = LocalDateTime.ofInstant(FIXED_INSTANT, FIXED_TIME_ZONE);

        Product product = createTestProduct("product", 10000, 2);
        OrderProduct orderProduct = createTestOrderProduct(product, 2);

        // when
        Order order = createTestOrder(member, orderDateTime, List.of(orderProduct));

        // then
        assertThat(order.getTotalPrice()).isEqualTo(20000);
    }

    private Member createTestMember() {
        return Member.builder()
                .name("member")
                .email("member@email.com")
                .role(USER)
                .profile("profile")
                .memberType(KAKAO)
                .build();
    }

    private Product createTestProduct(String name, int price, int stockQuantity) {
        return Product.builder()
                .name(name)
                .productType(PRODUCT_A)
                .productSellingStatus(SELLING)
                .price(price)
                .stockQuantity(stockQuantity)
                .build();
    }

    private OrderProduct createTestOrderProduct(Product product, int orderQuantity) {
        return OrderProduct.builder()
                .product(product)
                .orderQuantity(orderQuantity)
                .build();
    }

    private Order createTestOrder(Member member, LocalDateTime orderDateTime, List<OrderProduct> orderProducts) {
        return Order.builder()
                .member(member)
                .orderDateTime(orderDateTime)
                .orderStatus(INIT)
                .orderProducts(orderProducts)
                .build();
    }
}
