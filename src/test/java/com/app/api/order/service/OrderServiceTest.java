package com.app.api.order.service;

import com.app.api.common.PageResponse;
import com.app.api.order.dto.request.OrderCreateRequest;
import com.app.api.order.dto.response.OrderResponse;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

@SpringBootTest
class OrderServiceTest {

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
        Member member = createAndSaveTestMember("member", "member@email.com");
        Product product = createAndSaveTestProduct("product", 10000, 2);
        LocalDateTime orderDateTime = LocalDateTime.ofInstant(FIXED_INSTANT, FIXED_TIME_ZONE);

        Long productId = product.getId();
        OrderCreateRequest request = OrderCreateRequest.builder()
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
        Member member = createAndSaveTestMember();
        Product product = createAndSaveTestProduct(1);
        LocalDateTime orderDateTime = LocalDateTime.ofInstant(FIXED_INSTANT, FIXED_TIME_ZONE);

        Long productId = product.getId();
        OrderCreateRequest request = OrderCreateRequest.builder()
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
        Member member = createAndSaveTestMember();
        Product product = createAndSaveTestProduct(1);
        LocalDateTime orderDateTime = LocalDateTime.ofInstant(FIXED_INSTANT, FIXED_TIME_ZONE);
        Order order = createAndSaveTestOrder(member, orderDateTime, product, 1);
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
        Member member = createAndSaveTestMember();

        // when & then
        assertThatThrownBy(() -> orderService.cancelOrder(member.getId(), 1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ORDER_NOT_FOUND.getErrorMessage());
    }

    @DisplayName("주문을 등록하지 않은 회원 아이디로 취소를 시도할 경우, 예외가 발생한다.")
    @Test
    void cancelOrder_ForbiddenOrderCancellation() {
        // given
        Member member1 = createAndSaveTestMember("memberA", "memberA@email.com");
        Product product = createAndSaveTestProduct(1);
        LocalDateTime orderDateTime = LocalDateTime.ofInstant(FIXED_INSTANT, FIXED_TIME_ZONE);
        Order order = createAndSaveTestOrder(member1, orderDateTime, product, 1);

        Member member2 = createAndSaveTestMember("memberB", "memberB@email.com");

        // when & then
        assertThatThrownBy(() -> orderService.cancelOrder(member2.getId(), order.getId()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage(FORBIDDEN_ORDER_CANCELLATION.getErrorMessage());
    }

    @DisplayName("등록된 주문을 조회한다.")
    @Test
    void findOrders() {
        // given
        Member member1 = createAndSaveTestMember("memberA", "memberA@email.com");
        Member member2 = createAndSaveTestMember("memberB", "memberB@email.com");
        Product product = createAndSaveTestProduct("product", 10000, 3);

        LocalDateTime orderDateTime1 = LocalDateTime.ofInstant(FIXED_INSTANT, FIXED_TIME_ZONE);
        LocalDateTime orderDateTime2 = orderDateTime1.plus(1, MILLIS);

        createAndSaveTestOrder(member1, orderDateTime1, product, 1);
        createAndSaveTestOrder(member2, orderDateTime2, product, 2);

        PageRequest pageRequest = PageRequest.of(0, 2);

        // when
        PageResponse<OrderResponse> pageResponse = orderService.findOrders(pageRequest);

        // then
        List<OrderResponse> content = pageResponse.getContent();
        assertThat(content).hasSize(2);
        assertThat(content)
                .extracting("memberName", "orderDateTime", "orderStatus", "totalOrderPrice")
                .containsExactly(
                        tuple("memberA", orderDateTime1, INIT.name(), 10000),
                        tuple("memberB", orderDateTime2, INIT.name(), 20000)
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
        Member member1 = createAndSaveTestMember("memberA", "memberA@email.com");
        Member member2 = createAndSaveTestMember("memberB", "memberB@email.com");
        Product product = createAndSaveTestProduct("product", 10000, 3);

        LocalDateTime orderDateTime1 = LocalDateTime.ofInstant(FIXED_INSTANT, FIXED_TIME_ZONE);
        LocalDateTime orderDateTime2 = orderDateTime1.plus(1, MILLIS);

        createAndSaveTestOrder(member1, orderDateTime1, product, 1);
        createAndSaveTestOrder(member2, orderDateTime2, product, 2);

        PageRequest pageRequest = PageRequest.of(1, 2);

        // when
        PageResponse<OrderResponse> pageResponse = orderService.findOrders(pageRequest);

        // then
        assertThat(pageResponse.getContent()).isEmpty();
        assertThat(pageResponse.getSize()).isEqualTo(2);
        assertThat(pageResponse.getNumber()).isEqualTo(1);
        assertThat(pageResponse.getTotalElements()).isEqualTo(2);
        assertThat(pageResponse.getTotalPages()).isEqualTo(1);
    }

    private Member createAndSaveTestMember() {
        return createAndSaveTestMember("member", "member@email.com");
    }

    private Member createAndSaveTestMember(String name, String email) {
        return memberRepository.save(Member.builder()
                .name(name)
                .email(email)
                .role(USER)
                .profile("profile")
                .memberType(KAKAO)
                .build());
    }

    private Product createAndSaveTestProduct(int stockQuantity) {
        return createAndSaveTestProduct("product", 10000, stockQuantity);
    }

    private Product createAndSaveTestProduct(String name, int price, int stockQuantity) {
        return productRepository.save(Product.builder()
                .name(name)
                .productType(PRODUCT_A)
                .productSellingStatus(SELLING)
                .price(price)
                .stockQuantity(stockQuantity)
                .build());
    }

    private Order createAndSaveTestOrder(Member member, LocalDateTime orderDateTime, Product product, int orderQuantity) {
        OrderProduct orderProduct = OrderProduct.builder()
                .product(product)
                .orderQuantity(orderQuantity)
                .build();

        return orderRepository.save(Order.builder()
                .member(member)
                .orderDateTime(orderDateTime)
                .orderStatus(INIT)
                .orderProducts(List.of(orderProduct))
                .build());
    }
}
