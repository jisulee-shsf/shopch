package com.app.api.order.service;

import com.app.api.common.PageResponse;
import com.app.api.order.service.dto.request.OrderCreateServiceRequest;
import com.app.api.order.service.dto.request.OrderServiceSearchCondition;
import com.app.api.order.service.dto.response.OrderResponse;
import com.app.domain.member.entity.Member;
import com.app.domain.member.repository.MemberRepository;
import com.app.domain.order.entity.Order;
import com.app.domain.order.repository.OrderRepository;
import com.app.domain.orderProduct.entity.OrderProduct;
import com.app.domain.orderProduct.repository.OrderProductRepository;
import com.app.domain.product.entity.Product;
import com.app.domain.product.repository.ProductRepository;
import com.app.global.error.exception.EntityNotFoundException;
import com.app.global.error.exception.ForbiddenException;
import com.app.global.error.exception.OutOfStockException;
import com.app.support.IntegrationTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.app.domain.member.constant.MemberType.KAKAO;
import static com.app.domain.member.constant.Role.USER;
import static com.app.domain.order.constant.OrderStatus.CANCELED;
import static com.app.domain.order.constant.OrderStatus.INIT;
import static com.app.domain.product.constant.ProductSellingStatus.SELLING;
import static com.app.domain.product.constant.ProductType.PRODUCT_A;
import static com.app.fixture.TimeFixture.FIXED_INSTANT;
import static com.app.fixture.TimeFixture.FIXED_TIME_ZONE;
import static com.app.global.error.ErrorType.*;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class OrderServiceTest extends IntegrationTestSupport {

    @Autowired
    private OrderService orderService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @AfterEach
    void tearDown() {
        orderProductRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("주문을 등록한다.")
    @Test
    void createOrder() {
        // given
        Member member = createTestMember("member", "member@email.com");
        memberRepository.save(member);

        Product product = createTestProduct("product", 10000, 2);
        productRepository.save(product);

        LocalDateTime orderDateTime = LocalDateTime.ofInstant(FIXED_INSTANT, FIXED_TIME_ZONE);

        Long productId = product.getId();
        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder()
                .productId(productId)
                .orderQuantity(2)
                .build();

        // when
        OrderResponse response = orderService.createOrder(member.getId(), orderDateTime, request);

        // then
        Long orderId = response.getOrderId();

        assertThat(orderId).isNotNull();
        assertThat(response)
                .extracting("memberName", "orderDateTime", "orderStatus", "totalOrderPrice")
                .containsExactly("member", orderDateTime, INIT.name(), 20000);
        assertThat(response.getOrderProducts())
                .extracting("productId", "productName", "orderPrice", "orderQuantity")
                .containsExactly(
                        tuple(productId, "product", 10000, 2)
                );

        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        assertThat(optionalOrder)
                .isPresent()
                .get()
                .extracting("orderStatus")
                .isEqualTo(INIT);
    }

    @DisplayName("상품 재고 수량이 주문 수량보다 적을 때 등록을 시도할 경우, 예외가 발생한다.")
    @Test
    void createOrder_StockQuantityLessThanOrderQuantity() {
        // given
        Member member = createTestMember();
        memberRepository.save(member);

        Product product = createTestProduct(1);
        productRepository.save(product);

        LocalDateTime orderDateTime = LocalDateTime.ofInstant(FIXED_INSTANT, FIXED_TIME_ZONE);

        Long productId = product.getId();
        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder()
                .productId(productId)
                .orderQuantity(2)
                .build();

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(member.getId(), orderDateTime, request))
                .isInstanceOf(OutOfStockException.class)
                .hasMessage(OUT_OF_STOCK.getErrorMessage());
    }

    @DisplayName("등록된 주문을 취소한다.")
    @Test
    @Transactional
    void cancelOrder() {
        // given
        Member member = createTestMember();
        memberRepository.save(member);

        Product product = createTestProduct(1);
        productRepository.save(product);

        LocalDateTime orderDateTime = LocalDateTime.ofInstant(FIXED_INSTANT, FIXED_TIME_ZONE);
        Order order = createTestOrder(member, orderDateTime, product, 1);
        orderRepository.save(order);

        Long orderId = order.getId();

        // when
        orderService.cancelOrder(order.getMember().getId(), orderId);

        // then
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        assertThat(optionalOrder)
                .isPresent()
                .get()
                .extracting("orderStatus")
                .isEqualTo(CANCELED);

        Product foundProduct = optionalOrder.get().getOrderProducts().get(0).getProduct();
        assertThat(foundProduct)
                .extracting("stockQuantity")
                .isEqualTo(1);
    }

    @DisplayName("등록된 주문이 없을 때 취소를 시도할 경우, 예외가 발생한다.")
    @Test
    void cancelOrder_OrderNotFound() {
        // given
        Member member = createTestMember();
        memberRepository.save(member);

        // when & then
        assertThatThrownBy(() -> orderService.cancelOrder(member.getId(), 1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ORDER_NOT_FOUND.getErrorMessage());
    }

    @DisplayName("주문을 등록하지 않은 회원 아이디로 취소를 시도할 경우, 예외가 발생한다.")
    @Test
    void cancelOrder_ForbiddenOrderCancellation() {
        // given
        Member member1 = createTestMember("memberA", "memberA@email.com");
        Member member2 = createTestMember("memberB", "memberB@email.com");
        memberRepository.saveAll(List.of(member1, member2));

        Product product = createTestProduct(1);
        productRepository.save(product);

        LocalDateTime orderDateTime = LocalDateTime.ofInstant(FIXED_INSTANT, FIXED_TIME_ZONE);
        Order order = createTestOrder(member1, orderDateTime, product, 1);
        orderRepository.save(order);

        // when & then
        assertThatThrownBy(() -> orderService.cancelOrder(member2.getId(), order.getId()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage(FORBIDDEN_ORDER_CANCELLATION.getErrorMessage());
    }

    @DisplayName("검색 조건에 해당하는 주문과 페이징 결과를 조회한다.")
    @Test
    void findOrders() {
        // given
        Member member = createTestMember("member", "member@email.com");
        memberRepository.save(member);

        Product product = createTestProduct(3);
        productRepository.save(product);

        LocalDateTime orderDateTime1 = LocalDateTime.ofInstant(FIXED_INSTANT, FIXED_TIME_ZONE);
        LocalDateTime orderDateTime2 = orderDateTime1.plus(1, MILLIS);

        Order order1 = createTestOrder(member, orderDateTime1, product, 1);
        Order order2 = createTestOrder(member, orderDateTime2, product, 2);
        orderRepository.saveAll(List.of(order1, order2));

        OrderServiceSearchCondition searchCondition = OrderServiceSearchCondition.builder()
                .memberName("member")
                .orderStatus(INIT.name())
                .build();

        // when
        PageResponse<OrderResponse> pageResponse = orderService.findOrders(searchCondition, PageRequest.of(0, 2));

        // then
        List<OrderResponse> content = pageResponse.getContent();

        assertThat(content).hasSize(2);
        assertThat(content)
                .extracting("memberName", "orderDateTime", "orderStatus", "totalOrderPrice")
                .containsExactly(
                        tuple("member", orderDateTime1, INIT.name(), 10000),
                        tuple("member", orderDateTime2, INIT.name(), 20000)
                );

        assertThat(pageResponse.getSize()).isEqualTo(2);
        assertThat(pageResponse.getNumber()).isEqualTo(0);
        assertThat(pageResponse.getTotalElements()).isEqualTo(2);
        assertThat(pageResponse.getTotalPages()).isEqualTo(1);
    }

    @DisplayName("조회한 페이지에 컨텐츠가 없을 경우, 빈 페이지를 반환한다.")
    @Test
    void findOrders_NoContent() {
        // given
        Member member = createTestMember("member", "member@email.com");
        memberRepository.save(member);

        Product product = createTestProduct(3);
        productRepository.save(product);

        LocalDateTime orderDateTime1 = LocalDateTime.ofInstant(FIXED_INSTANT, FIXED_TIME_ZONE);
        LocalDateTime orderDateTime2 = orderDateTime1.plus(1, MILLIS);

        Order order1 = createTestOrder(member, orderDateTime1, product, 1);
        Order order2 = createTestOrder(member, orderDateTime2, product, 2);
        orderRepository.saveAll(List.of(order1, order2));

        OrderServiceSearchCondition searchCondition = OrderServiceSearchCondition.builder()
                .memberName("member")
                .orderStatus(INIT.name())
                .build();

        // when
        PageResponse<OrderResponse> pageResponse = orderService.findOrders(searchCondition, PageRequest.of(1, 2));

        // then
        assertThat(pageResponse.getContent()).isEmpty();
        assertThat(pageResponse.getSize()).isEqualTo(2);
        assertThat(pageResponse.getNumber()).isEqualTo(1);
        assertThat(pageResponse.getTotalElements()).isEqualTo(2);
        assertThat(pageResponse.getTotalPages()).isEqualTo(1);
    }

    private Member createTestMember() {
        return createTestMember("member", "member@email.com");
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
        return createTestProduct("product", 10000, stockQuantity);
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

    private Order createTestOrder(Member member, LocalDateTime orderDateTime, Product product, int orderQuantity) {
        OrderProduct orderProduct = OrderProduct.builder()
                .product(product)
                .orderQuantity(orderQuantity)
                .build();

        return Order.builder()
                .member(member)
                .orderDateTime(orderDateTime)
                .orderStatus(INIT)
                .orderProducts(List.of(orderProduct))
                .build();
    }
}
