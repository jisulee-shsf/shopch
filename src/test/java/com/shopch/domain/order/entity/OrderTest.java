package com.shopch.domain.order.entity;

import com.shopch.domain.member.entity.Member;
import com.shopch.domain.order.constant.OrderStatus;
import com.shopch.domain.orderProduct.entity.OrderProduct;
import com.shopch.domain.product.entity.Product;
import com.shopch.global.error.exception.AlreadyCanceledOrderException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.shopch.domain.member.constant.Role.USER;
import static com.shopch.domain.order.constant.OrderStatus.CANCELED;
import static com.shopch.domain.order.constant.OrderStatus.INIT;
import static com.shopch.domain.product.constant.ProductSellingStatus.SELLING;
import static com.shopch.domain.product.constant.ProductType.PRODUCT_1;
import static com.shopch.external.oauth.constant.OAuthProvider.KAKAO;
import static com.shopch.fixture.TimeFixture.INSTANT_NOW;
import static com.shopch.fixture.TimeFixture.TEST_TIME_ZONE;
import static com.shopch.global.error.ErrorCode.ALREADY_CANCELED_ORDER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class OrderTest {

    private static final String OAUTH_ID = "1";
    private static final String MEMBER_NAME = "member";
    private static final String MEMBER_EMAIL = "member@email.com";
    private static final String ID_NAME = "id";
    private static final String PRODUCT_NAME = "product";
    private static final int PRODUCT_PRICE = 10000;
    private static final int PRODUCT_STOCK_QUANTITY = 10;
    private static final int MINIMUM_ORDER_QUANTITY = 1;
    private static final LocalDateTime ORDERED_AT = LocalDateTime.ofInstant(INSTANT_NOW, TEST_TIME_ZONE);
    private static final Long MEMBER_1_ID = 1L;
    private static final Long MEMBER_2_ID = 2L;

    @DisplayName("주문 생성 시 주문 상태는 INIT이고, 총 주문 금액은 주문 상품별 금액의 합계이다.")
    @Test
    void create() {
        // given
        Member member = createMember();

        Product product = createProduct(PRODUCT_PRICE, PRODUCT_STOCK_QUANTITY);
        OrderProduct orderProduct = createOrderProduct(product, MINIMUM_ORDER_QUANTITY);

        // when
        Order order = Order.create(member, ORDERED_AT, List.of(orderProduct));

        // then
        assertThat(order)
                .extracting(
                        Order::getMember,
                        Order::getOrderStatus,
                        Order::getOrderedAt,
                        Order::getOrderProducts,
                        Order::getTotalPrice
                ).
                containsExactly(
                        member,
                        INIT,
                        ORDERED_AT,
                        List.of(orderProduct),
                        calculateTotalPrice(order.getOrderProducts())
                );
    }

    @DisplayName("주문 생성 시 주문이 회원을 참조하는 단방향 연관관계가 설정된다.")
    @Test
    void create_UnidirectionalRelationship() {
        // given
        Member member = createMember();

        Product product = createProduct(PRODUCT_PRICE, PRODUCT_STOCK_QUANTITY);
        OrderProduct orderProduct = createOrderProduct(product, MINIMUM_ORDER_QUANTITY);

        // when
        Order order = Order.create(member, ORDERED_AT, List.of(orderProduct));

        // then
        assertThat(order.getMember()).isEqualTo(member);
    }

    @DisplayName("주문 생성 시 주문과 주문 상품이 서로 참조하는 양방향 연관관계가 설정된다.")
    @Test
    void create_BidirectionalRelationship() {
        // given
        Member member = createMember();

        Product product = createProduct(PRODUCT_PRICE, PRODUCT_STOCK_QUANTITY);
        OrderProduct orderProduct = createOrderProduct(product, MINIMUM_ORDER_QUANTITY);

        // when
        Order order = Order.create(member, ORDERED_AT, List.of(orderProduct));

        // then
        assertThat(order.getOrderProducts()).contains(orderProduct);
        assertThat(orderProduct.getOrder()).isEqualTo(order);
    }

    @DisplayName("주문 취소 시 주문 상태를 INIT에서 CANCELED로 변경하고, 상품 재고 수량을 복구한다.")
    @Test
    void cancel() {
        // given
        Member member = createMember();

        Product product = createProduct(PRODUCT_PRICE, PRODUCT_STOCK_QUANTITY);
        OrderProduct orderProduct = createOrderProduct(product, MINIMUM_ORDER_QUANTITY);

        Order order = createOrder(member, ORDERED_AT, List.of(orderProduct));

        // when
        order.cancel();

        // then
        assertThat(order.getOrderStatus()).isEqualTo(CANCELED);
        assertThat(order.getOrderProducts())
                .extracting(op -> op.getProduct().getStockQuantity())
                .containsExactly(PRODUCT_STOCK_QUANTITY);
    }

    @DisplayName("이미 취소된 주문에 중복 취소를 시도할 경우, 예외가 발생한다.")
    @Test
    void cancel_AlreadyCanceledOrder() {
        // given
        Member member = createMember();

        Product product = createProduct(PRODUCT_PRICE, PRODUCT_STOCK_QUANTITY);
        OrderProduct orderProduct = createOrderProduct(product, MINIMUM_ORDER_QUANTITY);

        Order canceledOrder = createOrder(member, CANCELED, ORDERED_AT, List.of(orderProduct));

        // when & then
        assertThatThrownBy(canceledOrder::cancel)
                .isInstanceOf(AlreadyCanceledOrderException.class)
                .hasMessage(ALREADY_CANCELED_ORDER.getMessage());
    }

    @DisplayName("주문을 생성한 회원 아이디와 주어진 아이디가 같을 경우, true를 반환한다.")
    @Test
    void isOwner_SameMemberId() {
        // given
        Member member = createMember(MEMBER_1_ID);

        Product product = createProduct(PRODUCT_PRICE, PRODUCT_STOCK_QUANTITY);
        OrderProduct orderProduct = createOrderProduct(product, MINIMUM_ORDER_QUANTITY);

        Order order = createOrder(member, ORDERED_AT, List.of(orderProduct));

        // when & then
        assertThat(order.isOwner(MEMBER_1_ID)).isTrue();
    }

    @DisplayName("주문을 생성한 회원 아이디와 주어진 아이디가 다를 경우, true를 반환한다.")
    @Test
    void isNotOwner_DifferentMemberId() {
        // given
        Member member = createMember(MEMBER_1_ID);

        Product product = createProduct(PRODUCT_PRICE, PRODUCT_STOCK_QUANTITY);
        OrderProduct orderProduct = createOrderProduct(product, MINIMUM_ORDER_QUANTITY);

        Order order = createOrder(member, ORDERED_AT, List.of(orderProduct));

        // when & then
        assertAll(
                () -> assertThat(order.isNotOwner(MEMBER_2_ID)).isTrue(),
                () -> assertThat(order.isNotOwner(null)).isTrue()
        );
    }

    private Member createMember() {
        return createMember(null);
    }

    private Member createMember(Long memberId) {
        Member member = Member.builder()
                .oauthId(OAUTH_ID)
                .name(MEMBER_NAME)
                .email(MEMBER_EMAIL)
                .role(USER)
                .oauthProvider(KAKAO)
                .build();

        setField(member, ID_NAME, memberId);
        return member;
    }

    private Product createProduct(int price, int stockQuantity) {
        return Product.builder()
                .name(PRODUCT_NAME)
                .productType(PRODUCT_1)
                .productSellingStatus(SELLING)
                .price(price)
                .stockQuantity(stockQuantity)
                .build();
    }

    private OrderProduct createOrderProduct(Product product, int orderQuantity) {
        return OrderProduct.builder()
                .product(product)
                .orderQuantity(orderQuantity)
                .build();
    }

    private Order createOrder(Member member, LocalDateTime orderedAt, List<OrderProduct> orderProducts) {
        return createOrder(member, INIT, orderedAt, orderProducts);
    }

    private Order createOrder(Member member, OrderStatus orderStatus, LocalDateTime orderedAt, List<OrderProduct> orderProducts) {
        return Order.builder()
                .member(member)
                .orderStatus(orderStatus)
                .orderedAt(orderedAt)
                .orderProducts(orderProducts)
                .build();
    }

    private int calculateTotalPrice(List<OrderProduct> orderProducts) {
        return orderProducts.stream()
                .mapToInt(op -> op.getOrderPrice() * op.getOrderQuantity())
                .sum();
    }
}
