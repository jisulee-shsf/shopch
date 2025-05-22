package com.app.domain.order.repository;

import com.app.api.order.service.dto.request.OrderServiceSearchCondition;
import com.app.domain.member.entity.Member;
import com.app.domain.member.repository.MemberRepository;
import com.app.domain.order.constant.OrderStatus;
import com.app.domain.order.entity.Order;
import com.app.domain.orderProduct.entity.OrderProduct;
import com.app.domain.orderProduct.repository.OrderProductRepository;
import com.app.domain.product.entity.Product;
import com.app.domain.product.repository.ProductRepository;
import com.app.support.IntegrationTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

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
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.assertj.core.api.Assertions.assertThat;

class OrderRepositoryImplTest extends IntegrationTestSupport {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @AfterEach
    void tearDown() {
        orderProductRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("검색 조건으로 회원 이름만 있을 경우, 회원 이름이 같은 주문과 페이징 결과를 조회한다.")
    @Test
    void findAllBySearchCondition_MemberName() {
        // given
        Member member1 = createTestMember("memberA", "memberA@email.com");
        Member member2 = createTestMember("memberB", "memberB@email.com");
        memberRepository.saveAll(List.of(member1, member2));

        Product product = createTestProduct(3);
        productRepository.save(product);

        LocalDateTime orderDateTime1 = LocalDateTime.ofInstant(FIXED_INSTANT, FIXED_TIME_ZONE);
        LocalDateTime orderDateTime2 = orderDateTime1.plus(1, MILLIS);
        Order order1 = createTestOrder(member1, orderDateTime1, product, 1, INIT);
        Order order2 = createTestOrder(member2, orderDateTime2, product, 1, INIT);
        Order order3 = createTestOrder(member2, orderDateTime2, product, 1, CANCELED);
        orderRepository.saveAll(List.of(order1, order2, order3));

        OrderServiceSearchCondition searchCondition = OrderServiceSearchCondition.builder()
                .memberName("memberA")
                .orderStatus(null)
                .build();

        // when
        Page<Order> pageOrders = orderRepository.findAllBySearchCondition(searchCondition, PageRequest.of(0, 3));

        // then
        List<Order> content = pageOrders.getContent();
        assertThat(content).hasSize(1);
        assertThat(content)
                .extracting(Order::getMember)
                .extracting(Member::getId)
                .containsExactly(member1.getId());

        assertThat(pageOrders.getSize()).isEqualTo(3);
        assertThat(pageOrders.getNumber()).isEqualTo(0);
        assertThat(pageOrders.getTotalElements()).isEqualTo(1);
        assertThat(pageOrders.getTotalPages()).isEqualTo(1);
    }

    @DisplayName("검색 조건으로 주문 상태만 있을 경우, 주문 상태가 같은 주문과 페이징 결과를 조회한다.")
    @Test
    void findAllBySearchCondition_OrderStatus() {
        // given
        Member member1 = createTestMember("memberA", "memberA@email.com");
        Member member2 = createTestMember("memberB", "memberB@email.com");
        memberRepository.saveAll(List.of(member1, member2));

        Product product = createTestProduct(3);
        productRepository.save(product);

        LocalDateTime orderDateTime1 = LocalDateTime.ofInstant(FIXED_INSTANT, FIXED_TIME_ZONE);
        LocalDateTime orderDateTime2 = orderDateTime1.plus(1, MILLIS);
        Order order1 = createTestOrder(member1, orderDateTime1, product, 1, INIT);
        Order order2 = createTestOrder(member2, orderDateTime2, product, 1, INIT);
        Order order3 = createTestOrder(member2, orderDateTime2, product, 1, CANCELED);
        orderRepository.saveAll(List.of(order1, order2, order3));

        OrderServiceSearchCondition searchCondition = OrderServiceSearchCondition.builder()
                .memberName(null)
                .orderStatus(INIT.name())
                .build();

        // when
        Page<Order> pageOrders = orderRepository.findAllBySearchCondition(searchCondition, PageRequest.of(0, 3));

        // then
        List<Order> content = pageOrders.getContent();
        assertThat(content).hasSize(2);
        assertThat(content)
                .extracting(Order::getOrderStatus)
                .containsOnly(INIT);

        assertThat(pageOrders.getSize()).isEqualTo(3);
        assertThat(pageOrders.getNumber()).isEqualTo(0);
        assertThat(pageOrders.getTotalElements()).isEqualTo(2);
        assertThat(pageOrders.getTotalPages()).isEqualTo(1);
    }

    @DisplayName("검색 조건이 모두 있을 경우, 조건에 해당하는 주문과 페이징 결과를 조회한다.")
    @Test
    void findAllBySearchCondition_MemberNameAndOrderStatus() {
        // given
        Member member1 = createTestMember("memberA", "memberA@email.com");
        Member member2 = createTestMember("memberB", "memberB@email.com");
        memberRepository.saveAll(List.of(member1, member2));

        Product product = createTestProduct(3);
        productRepository.save(product);

        LocalDateTime orderDateTime1 = LocalDateTime.ofInstant(FIXED_INSTANT, FIXED_TIME_ZONE);
        LocalDateTime orderDateTime2 = orderDateTime1.plus(1, MILLIS);
        Order order1 = createTestOrder(member1, orderDateTime1, product, 1, INIT);
        Order order2 = createTestOrder(member2, orderDateTime2, product, 1, INIT);
        Order order3 = createTestOrder(member2, orderDateTime2, product, 1, CANCELED);
        orderRepository.saveAll(List.of(order1, order2, order3));

        OrderServiceSearchCondition searchCondition = OrderServiceSearchCondition.builder()
                .memberName("memberA")
                .orderStatus(INIT.name())
                .build();

        // when
        Page<Order> pageOrders = orderRepository.findAllBySearchCondition(searchCondition, PageRequest.of(0, 3));

        // then
        List<Order> content = pageOrders.getContent();
        assertThat(content).hasSize(1);
        assertThat(content)
                .extracting(Order::getMember)
                .extracting(Member::getId)
                .containsExactly(member1.getId());
        assertThat(content)
                .extracting(Order::getOrderStatus)
                .containsExactly(INIT);

        assertThat(pageOrders.getSize()).isEqualTo(3);
        assertThat(pageOrders.getNumber()).isEqualTo(0);
        assertThat(pageOrders.getTotalElements()).isEqualTo(1);
        assertThat(pageOrders.getTotalPages()).isEqualTo(1);
    }

    @DisplayName("검색 조건이 모두 없을 경우, 등록된 모든 주문과 페이징 결과를 조회한다.")
    @Test
    void findAllBySearchCondition_None() {
        // given
        Member member1 = createTestMember("memberA", "memberA@email.com");
        Member member2 = createTestMember("memberB", "memberB@email.com");
        memberRepository.saveAll(List.of(member1, member2));

        Product product = createTestProduct(3);
        productRepository.save(product);

        LocalDateTime orderDateTime1 = LocalDateTime.ofInstant(FIXED_INSTANT, FIXED_TIME_ZONE);
        LocalDateTime orderDateTime2 = orderDateTime1.plus(1, MILLIS);
        Order order1 = createTestOrder(member1, orderDateTime1, product, 1, INIT);
        Order order2 = createTestOrder(member2, orderDateTime2, product, 1, INIT);
        Order order3 = createTestOrder(member2, orderDateTime2, product, 1, CANCELED);
        orderRepository.saveAll(List.of(order1, order2, order3));

        OrderServiceSearchCondition searchCondition = OrderServiceSearchCondition.builder()
                .memberName(null)
                .orderStatus(null)
                .build();

        // when
        Page<Order> pageOrders = orderRepository.findAllBySearchCondition(searchCondition, PageRequest.of(0, 3));

        // then
        List<Order> content = pageOrders.getContent();
        assertThat(content).hasSize(3);
        assertThat(content)
                .extracting(Order::getMember)
                .extracting(Member::getId)
                .containsOnly(member1.getId(), member2.getId());
        assertThat(content)
                .extracting(Order::getOrderStatus)
                .containsOnly(INIT, CANCELED);

        assertThat(pageOrders.getSize()).isEqualTo(3);
        assertThat(pageOrders.getNumber()).isEqualTo(0);
        assertThat(pageOrders.getTotalElements()).isEqualTo(3);
        assertThat(pageOrders.getTotalPages()).isEqualTo(1);
    }

    private Member createTestMember(String name, String email) {
        return Member.builder()
                .name(name)
                .email(email)
                .role(USER)
                .profile("profile")
                .memberType(KAKAO)
                .build();
    }

    private Product createTestProduct(int stockQuantity) {
        return Product.builder()
                .name("product")
                .productType(PRODUCT_A)
                .productSellingStatus(SELLING)
                .price(10000)
                .stockQuantity(stockQuantity)
                .build();
    }

    private Order createTestOrder(Member member, LocalDateTime orderDateTime, Product product, int orderQuantity, OrderStatus orderStatus) {
        OrderProduct orderProduct = OrderProduct.builder()
                .product(product)
                .orderQuantity(orderQuantity)
                .build();

        return Order.builder()
                .member(member)
                .orderDateTime(orderDateTime)
                .orderStatus(orderStatus)
                .orderProducts(List.of(orderProduct))
                .build();
    }
}
