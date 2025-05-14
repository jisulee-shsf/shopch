package com.app.domain.order.entity;

import com.app.domain.member.entity.Member;
import com.app.domain.order.constant.OrderStatus;
import com.app.domain.orderProduct.entity.OrderProduct;
import com.app.domain.product.entity.Product;
import com.app.global.error.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.app.domain.member.constant.MemberType.KAKAO;
import static com.app.domain.member.constant.Role.USER;
import static com.app.domain.order.constant.OrderStatus.CANCELED;
import static com.app.domain.order.constant.OrderStatus.INIT;
import static com.app.domain.product.constant.ProductSellingStatus.SELLING;
import static com.app.domain.product.constant.ProductType.PRODUCT_A;
import static com.app.fixture.TimeFixture.FIXED_INSTANT;
import static com.app.fixture.TimeFixture.FIXED_TIME_ZONE;
import static com.app.global.error.ErrorType.ALREADY_CANCELED_ORDER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    @DisplayName("주문 생성 시 주문 상태는 INIT이다.")
    @Test
    void create_orderStatus() {
        // given
        Member member = createTestMember();
        LocalDateTime orderDateTime = LocalDateTime.ofInstant(FIXED_INSTANT, FIXED_TIME_ZONE);

        Product product = createTestProduct();
        OrderProduct orderProduct = createTestOrderProduct(product);

        // when
        Order order = Order.create(member, orderDateTime, List.of(orderProduct));

        // then
        assertThat(order.getOrderStatus()).isEqualTo(INIT);
    }

    @DisplayName("주문 생성 시 주문이 회원을 참조하는 단방향 연관관계가 설정된다.")
    @Test
    void create_member() {
        // given
        Member member = createTestMember();
        LocalDateTime orderDateTime = LocalDateTime.ofInstant(FIXED_INSTANT, FIXED_TIME_ZONE);

        Product product = createTestProduct();
        OrderProduct orderProduct = createTestOrderProduct(product);

        // when
        Order order = Order.create(member, orderDateTime, List.of(orderProduct));

        // then
        assertThat(order.getMember()).isEqualTo(member);
    }

    @DisplayName("주문 생성 시 주문과 주문 상품이 서로 참조하는 양방향 연관관계가 설정된다.")
    @Test
    void create_changeOrderProduct() {
        // given
        Member member = createTestMember();
        LocalDateTime orderDateTime = LocalDateTime.ofInstant(FIXED_INSTANT, FIXED_TIME_ZONE);

        Product product = createTestProduct();
        OrderProduct orderProduct = createTestOrderProduct(product);

        // when
        Order order = Order.create(member, orderDateTime, List.of(orderProduct));

        // then
        assertThat(order.getOrderProducts().contains(orderProduct)).isTrue();
        assertThat(orderProduct.getOrder()).isEqualTo(order);
    }

    @DisplayName("주문 생성 시 총 주문 금액은 주문 상품별 총 금액의 합계이다.")
    @Test
    void create_getTotalOrderPrice() {
        // given
        Member member = createTestMember();
        LocalDateTime orderDateTime = LocalDateTime.ofInstant(FIXED_INSTANT, FIXED_TIME_ZONE);

        Product product = createTestProduct(10000, 2);
        OrderProduct orderProduct1 = createTestOrderProduct(product, 1);
        OrderProduct orderProduct2 = createTestOrderProduct(product, 1);

        // when
        Order order = Order.create(member, orderDateTime, List.of(orderProduct1, orderProduct2));

        // then
        assertThat(order.getTotalOrderPrice()).isEqualTo(20000);
    }

    @DisplayName("주문 취소 시 주문 상태를 INIT에서 CANCELED로 변경한 후, 상품 재고 수량을 주문 수량만큼 복구한다.")
    @Test
    void cancel() {
        // given
        Member member = createTestMember();
        LocalDateTime orderDateTime = LocalDateTime.ofInstant(FIXED_INSTANT, FIXED_TIME_ZONE);

        Product product = createTestProduct(10000, 1);
        OrderProduct orderProduct = createTestOrderProduct(product, 1);
        Order order = createTestOrder(member, orderDateTime, List.of(orderProduct));

        // when
        order.cancel();

        // then
        assertThat(order.getOrderStatus()).isEqualTo(CANCELED);
        assertThat(order.getOrderProducts().get(0).getProduct().getStockQuantity()).isEqualTo(1);
    }

    @DisplayName("이미 취소된 주문에 대해 중복 취소를 시도할 경우, 예외가 발생한다.")
    @Test
    void cancel_AlreadyCanceledOrder() {
        // given
        Member member = createTestMember();
        LocalDateTime orderDateTime = LocalDateTime.ofInstant(FIXED_INSTANT, FIXED_TIME_ZONE);

        Product product = createTestProduct(10000, 1);
        OrderProduct orderProduct = createTestOrderProduct(product, 1);
        Order canceledOrder = createTestOrder(member, orderDateTime, List.of(orderProduct), CANCELED);

        // when & then
        assertThatThrownBy(() -> canceledOrder.cancel())
                .isInstanceOf(BusinessException.class)
                .hasMessage(ALREADY_CANCELED_ORDER.getErrorMessage());
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

    private Product createTestProduct() {
        return createTestProduct(10000, 1);
    }

    private Product createTestProduct(int price, int stockQuantity) {
        return Product.builder()
                .name("product")
                .productType(PRODUCT_A)
                .productSellingStatus(SELLING)
                .price(price)
                .stockQuantity(stockQuantity)
                .build();
    }

    private OrderProduct createTestOrderProduct(Product product) {
        return createTestOrderProduct(product, 1);
    }

    private OrderProduct createTestOrderProduct(Product product, int orderQuantity) {
        return OrderProduct.builder()
                .product(product)
                .orderQuantity(orderQuantity)
                .build();
    }

    private Order createTestOrder(Member member, LocalDateTime orderDateTime, List<OrderProduct> orderProducts) {
        return createTestOrder(member, orderDateTime, orderProducts, INIT);
    }

    private Order createTestOrder(Member member, LocalDateTime orderDateTime, List<OrderProduct> orderProducts, OrderStatus orderStatus) {
        return Order.builder()
                .member(member)
                .orderDateTime(orderDateTime)
                .orderStatus(orderStatus)
                .orderProducts(orderProducts)
                .build();
    }
}
