package com.shopch.domain.order.repository;

import com.shopch.api.order.service.dto.request.OrderServiceSearchCondition;
import com.shopch.domain.member.entity.Member;
import com.shopch.domain.member.repository.MemberRepository;
import com.shopch.domain.order.constant.OrderStatus;
import com.shopch.domain.order.entity.Order;
import com.shopch.domain.orderProduct.entity.OrderProduct;
import com.shopch.domain.product.entity.Product;
import com.shopch.domain.product.repository.ProductRepository;
import com.shopch.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.shopch.domain.member.constant.Role.USER;
import static com.shopch.domain.order.constant.OrderStatus.CANCELED;
import static com.shopch.domain.order.constant.OrderStatus.INIT;
import static com.shopch.domain.product.constant.ProductSellingStatus.SELLING;
import static com.shopch.domain.product.constant.ProductType.PRODUCT_1;
import static com.shopch.external.oauth.constant.OAuthProvider.KAKAO;
import static com.shopch.fixture.TimeFixture.*;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@Transactional
class OrderRepositoryImplTest extends IntegrationTestSupport {

    private static final String OAUTH_ID = "1";
    private static final String MEMBER_1_NAME = "member1";
    private static final String MEMBER_2_NAME = "member2";
    private static final String MEMBER_EMAIL = "member@email.com";
    private static final String PRODUCT_NAME = "product";
    private static final int PRODUCT_PRICE = 10000;
    private static final int PRODUCT_STOCK_QUANTITY = 10;
    private static final int MINIMUM_ORDER_QUANTITY = 1;
    private static final LocalDateTime ORDERED_AT = LocalDateTime.ofInstant(INSTANT_NOW, TEST_TIME_ZONE);
    private static final int EXPECTED_SIZE_1 = 1;
    private static final int EXPECTED_SIZE_2 = 2;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @DisplayName("검색 조건으로 회원 이름이 있을 경우, 회원 이름이 같은 주문과 페이징 정보를 조회한다.")
    @Test
    void findAllBySearchCondition_MemberNameCondition() {
        // given
        Member member1 = createMember(MEMBER_1_NAME);
        Member member2 = createMember(MEMBER_2_NAME);
        memberRepository.saveAll(List.of(member1, member2));

        Product product = createProduct(PRODUCT_PRICE, PRODUCT_STOCK_QUANTITY);
        productRepository.save(product);

        OrderProduct orderProduct1 = createOrderProduct(product, MINIMUM_ORDER_QUANTITY);
        OrderProduct orderProduct2 = createOrderProduct(product, MINIMUM_ORDER_QUANTITY);

        Order order1 = createOrder(member1, CANCELED, ORDERED_AT, List.of(orderProduct1));
        Order order2 = createOrder(member2, INIT, ORDERED_AT.plus(ONE_SECOND_IN_MILLIS, MILLIS), List.of(orderProduct2));
        orderRepository.saveAll(List.of(order1, order2));

        OrderServiceSearchCondition searchCondition = OrderServiceSearchCondition.builder()
                .memberName(member1.getName())
                .orderStatus(null)
                .build();

        // when
        Page<Order> orders = orderRepository.findAllBySearchCondition(searchCondition, PageRequest.of(0, 2));

        // then
        List<Order> content = orders.getContent();
        assertThat(content).hasSize(EXPECTED_SIZE_1)
                .extracting(
                        o -> o.getMember().getId(),
                        Order::getOrderStatus,
                        Order::getOrderedAt,
                        Order::getTotalPrice,
                        o -> o.getOrderProducts().stream()
                                .map(OrderProduct::getId)
                                .toList()
                )
                .containsExactly(
                        tuple(
                                member1.getId(),
                                CANCELED,
                                ORDERED_AT,
                                calculateTotalPrice(order1.getOrderProducts()),
                                List.of(orderProduct1.getId())
                        )
                );

        assertThat(orders.getSize()).isEqualTo(2);
        assertThat(orders.getNumber()).isEqualTo(0);
        assertThat(orders.getTotalElements()).isEqualTo(1);
        assertThat(orders.getTotalPages()).isEqualTo(1);
    }

    @DisplayName("검색 조건으로 주문 상태가 있을 경우, 주문 상태가 같은 주문과 페이징 정보를 조회한다.")
    @Test
    void findAllBySearchCondition_OrderStatusCondition() {
        // given
        Member member1 = createMember(MEMBER_1_NAME);
        Member member2 = createMember(MEMBER_2_NAME);
        memberRepository.saveAll(List.of(member1, member2));

        Product product = createProduct(PRODUCT_PRICE, PRODUCT_STOCK_QUANTITY);
        productRepository.save(product);

        OrderProduct orderProduct1 = createOrderProduct(product, MINIMUM_ORDER_QUANTITY);
        OrderProduct orderProduct2 = createOrderProduct(product, MINIMUM_ORDER_QUANTITY);

        Order order1 = createOrder(member1, CANCELED, ORDERED_AT, List.of(orderProduct1));
        Order order2 = createOrder(member2, INIT, ORDERED_AT.plus(ONE_SECOND_IN_MILLIS, MILLIS), List.of(orderProduct2));
        orderRepository.saveAll(List.of(order1, order2));

        OrderServiceSearchCondition searchCondition = OrderServiceSearchCondition.builder()
                .memberName(null)
                .orderStatus(INIT)
                .build();

        // when
        Page<Order> orders = orderRepository.findAllBySearchCondition(searchCondition, PageRequest.of(0, 2));

        // then
        assertThat(orders.getContent()).hasSize(EXPECTED_SIZE_1)
                .extracting(
                        o -> o.getMember().getId(),
                        Order::getOrderStatus,
                        Order::getOrderedAt,
                        Order::getTotalPrice,
                        o -> o.getOrderProducts().stream()
                                .map(OrderProduct::getId)
                                .toList()
                )
                .containsExactly(
                        tuple(
                                member2.getId(),
                                INIT,
                                ORDERED_AT.plus(ONE_SECOND_IN_MILLIS, MILLIS),
                                calculateTotalPrice(order2.getOrderProducts()),
                                List.of(orderProduct2.getId())
                        )
                );

        assertThat(orders.getSize()).isEqualTo(2);
        assertThat(orders.getNumber()).isEqualTo(0);
        assertThat(orders.getTotalElements()).isEqualTo(1);
        assertThat(orders.getTotalPages()).isEqualTo(1);
    }

    @DisplayName("검색 조건이 없을 경우, 등록된 모든 주문과 페이징 정보를 조회한다.")
    @Test
    void findAllBySearchCondition_NoCondition() {
        // given
        Member member1 = createMember(MEMBER_1_NAME);
        Member member2 = createMember(MEMBER_2_NAME);
        memberRepository.saveAll(List.of(member1, member2));

        Product product = createProduct(PRODUCT_PRICE, PRODUCT_STOCK_QUANTITY);
        productRepository.save(product);

        OrderProduct orderProduct1 = createOrderProduct(product, MINIMUM_ORDER_QUANTITY);
        OrderProduct orderProduct2 = createOrderProduct(product, MINIMUM_ORDER_QUANTITY);

        Order order1 = createOrder(member1, CANCELED, ORDERED_AT, List.of(orderProduct1));
        Order order2 = createOrder(member2, INIT, ORDERED_AT.plus(ONE_SECOND_IN_MILLIS, MILLIS), List.of(orderProduct2));
        orderRepository.saveAll(List.of(order1, order2));

        OrderServiceSearchCondition searchCondition = OrderServiceSearchCondition.builder()
                .memberName(null)
                .orderStatus(null)
                .build();

        // when
        Page<Order> orders = orderRepository.findAllBySearchCondition(searchCondition, PageRequest.of(0, 2));

        // then
        assertThat(orders.getContent()).hasSize(EXPECTED_SIZE_2)
                .extracting(
                        o -> o.getMember().getId(),
                        Order::getOrderStatus,
                        Order::getOrderedAt,
                        Order::getTotalPrice,
                        o -> o.getOrderProducts().stream()
                                .map(OrderProduct::getId)
                                .toList()
                )
                .containsExactly(
                        tuple(
                                member1.getId(),
                                CANCELED,
                                ORDERED_AT,
                                calculateTotalPrice(order1.getOrderProducts()),
                                List.of(orderProduct1.getId())
                        ),
                        tuple(
                                member2.getId(),
                                INIT,
                                ORDERED_AT.plus(ONE_SECOND_IN_MILLIS, MILLIS),
                                calculateTotalPrice(order2.getOrderProducts()),
                                List.of(orderProduct2.getId())
                        )
                );

        assertThat(orders.getSize()).isEqualTo(2);
        assertThat(orders.getNumber()).isEqualTo(0);
        assertThat(orders.getTotalElements()).isEqualTo(2);
        assertThat(orders.getTotalPages()).isEqualTo(1);
    }

    private Member createMember(String name) {
        return Member.builder()
                .oauthId(OAUTH_ID)
                .name(name)
                .email(MEMBER_EMAIL)
                .role(USER)
                .oauthProvider(KAKAO)
                .build();
    }

    private Product createProduct(int productPrice, int stockQuantity) {
        return Product.builder()
                .name(PRODUCT_NAME)
                .productType(PRODUCT_1)
                .productSellingStatus(SELLING)
                .price(productPrice)
                .stockQuantity(stockQuantity)
                .build();
    }

    private OrderProduct createOrderProduct(Product product, int orderQuantity) {
        return OrderProduct.builder()
                .product(product)
                .orderQuantity(orderQuantity)
                .build();
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
