package com.shopch.api.order.service;

import com.shopch.api.common.dto.PageResponse;
import com.shopch.api.order.service.dto.request.OrderCreateServiceRequest;
import com.shopch.api.order.service.dto.request.OrderServiceSearchCondition;
import com.shopch.api.order.service.dto.response.OrderProductResponse;
import com.shopch.api.order.service.dto.response.OrderResponse;
import com.shopch.domain.member.entity.Member;
import com.shopch.domain.member.repository.MemberRepository;
import com.shopch.domain.order.entity.Order;
import com.shopch.domain.order.repository.OrderRepository;
import com.shopch.domain.orderProduct.entity.OrderProduct;
import com.shopch.domain.orderProduct.repository.OrderProductRepository;
import com.shopch.domain.product.entity.Product;
import com.shopch.domain.product.repository.ProductRepository;
import com.shopch.global.error.exception.EntityNotFoundException;
import com.shopch.global.error.exception.ForbiddenException;
import com.shopch.global.error.exception.InsufficientStockException;
import com.shopch.support.IntegrationTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import static com.shopch.global.error.ErrorCode.*;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.assertj.core.api.Assertions.*;

class OrderServiceTest extends IntegrationTestSupport {

    private static final String OAUTH_ID = "1";
    private static final String MEMBER_NAME = "member";
    private static final String MEMBER_EMAIL = "member@email.com";
    private static final String PRODUCT_NAME = "product";
    private static final int PRODUCT_PRICE = 10000;
    private static final int PRODUCT_STOCK_QUANTITY = 10;
    private static final int MINIMUM_ORDER_QUANTITY = 1;
    private static final LocalDateTime ORDERED_AT = LocalDateTime.ofInstant(INSTANT_NOW, TEST_TIME_ZONE);
    private static final Long NON_EXISTENT_ID = 1L;
    private static final int EXPECTED_SIZE_1 = 1;
    private static final int EXPECTED_SIZE_2 = 2;
    private static final Pageable DEFAULT_PAGE_REQUEST = PageRequest.of(0, 2);

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

    @DisplayName("주문 수량만큼 상품 재고 수량을 감소시켜 주문을 생성한다.")
    @Test
    void createOrder() {
        // given
        Member member = createMember(MEMBER_NAME);
        memberRepository.save(member);

        Product product = createProduct(PRODUCT_NAME, PRODUCT_PRICE, PRODUCT_STOCK_QUANTITY);
        productRepository.save(product);

        Long productId = product.getId();
        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder()
                .productId(productId)
                .orderQuantity(MINIMUM_ORDER_QUANTITY)
                .build();

        // when
        OrderResponse response = orderService.createOrder(member.getId(), request, ORDERED_AT);

        // then
        assertThat(response.getOrderId()).isNotNull();
        assertThat(response)
                .extracting(
                        OrderResponse::getMemberName,
                        OrderResponse::getOrderedAt,
                        OrderResponse::getOrderStatus,
                        OrderResponse::getTotalPrice
                )
                .containsExactly(
                        MEMBER_NAME,
                        ORDERED_AT,
                        INIT.name(),
                        PRODUCT_PRICE * MINIMUM_ORDER_QUANTITY
                );

        assertThat(response.getOrderProducts())
                .extracting(
                        OrderProductResponse::getProductId,
                        OrderProductResponse::getProductName,
                        OrderProductResponse::getOrderPrice,
                        OrderProductResponse::getOrderQuantity
                )
                .containsExactly(
                        tuple(
                                productId,
                                PRODUCT_NAME,
                                PRODUCT_PRICE,
                                MINIMUM_ORDER_QUANTITY
                        )
                );

        assertThat(orderRepository.findAll()).hasSize(EXPECTED_SIZE_1);

        assertThat(productRepository.findAll()).hasSize(EXPECTED_SIZE_1)
                .extracting(Product::getStockQuantity)
                .containsExactly(PRODUCT_STOCK_QUANTITY - MINIMUM_ORDER_QUANTITY);
    }

    @DisplayName("상품 재고 수량보다 주문 수량이 많을 때 주문 생성을 시도할 경우, 예외가 발생한다.")
    @Test
    void createOrder_InsufficientStock() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Product product = createProduct(PRODUCT_STOCK_QUANTITY);
        productRepository.save(product);

        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder()
                .productId(product.getId())
                .orderQuantity(PRODUCT_STOCK_QUANTITY + MINIMUM_ORDER_QUANTITY)
                .build();

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(member.getId(), request, ORDERED_AT))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessage(INSUFFICIENT_STOCK.getMessage());
    }

    @DisplayName("등록된 상품이 없을 때 주문 생성을 시도할 경우, 예외가 발생한다.")
    @Test
    void createOrder_ProductNotFound() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder()
                .productId(NON_EXISTENT_ID)
                .orderQuantity(MINIMUM_ORDER_QUANTITY)
                .build();

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(member.getId(), request, ORDERED_AT))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(PRODUCT_NOT_FOUND.getMessage());
    }

    @DisplayName("등록된 회원이 없을 때 주문 생성을 시도할 경우, 예외가 발생한다.")
    @Test
    void createOrder_MemberNotFound() {
        // given
        Product product = createProduct(PRODUCT_STOCK_QUANTITY);
        productRepository.save(product);

        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder()
                .productId(product.getId())
                .orderQuantity(MINIMUM_ORDER_QUANTITY)
                .build();

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(NON_EXISTENT_ID, request, ORDERED_AT))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(MEMBER_NOT_FOUND.getMessage());
    }

    @DisplayName("주문 상태를 CANCELED로 변경하고 상품 재고 수량을 복원해 주문을 취소한다.")
    @Test
    @Transactional
    void cancelOrder() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Product product = createProduct(PRODUCT_STOCK_QUANTITY);
        productRepository.save(product);

        OrderProduct orderProduct = createOrderProduct(product, MINIMUM_ORDER_QUANTITY);
        Order order = createOrder(member, ORDERED_AT, List.of(orderProduct));
        orderRepository.save(order);

        Long orderId = order.getId();

        // when
        orderService.cancelOrder(member.getId(), orderId);

        // then
        assertThat(orderRepository.findAll()).hasSize(EXPECTED_SIZE_1)
                .extracting(Order::getOrderStatus)
                .containsExactly(CANCELED);

        assertThat(productRepository.findAll()).hasSize(EXPECTED_SIZE_1)
                .extracting(Product::getStockQuantity)
                .containsExactly(PRODUCT_STOCK_QUANTITY);
    }

    @DisplayName("등록된 주문이 없을 때 주문 취소를 시도할 경우, 예외가 발생한다.")
    @Test
    void cancelOrder_OrderNotFound() {
        // given
        Member member = createMember(MEMBER_NAME);
        memberRepository.save(member);

        // when & then
        assertThatThrownBy(() -> orderService.cancelOrder(member.getId(), NON_EXISTENT_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ORDER_NOT_FOUND.getMessage());
    }

    @DisplayName("주문을 생성하지 않은 회원 아이디로 주문 취소를 시도할 경우, 예외가 발생한다.")
    @Test
    void cancelOrder_OrderAccessDenied() {
        // given
        Member member1 = createMember();
        Member member2 = createMember();
        memberRepository.saveAll(List.of(member1, member2));

        Product product = createProduct(PRODUCT_STOCK_QUANTITY);
        productRepository.save(product);

        OrderProduct orderProduct = createOrderProduct(product, MINIMUM_ORDER_QUANTITY);
        Order order = createOrder(member1, ORDERED_AT, List.of(orderProduct));
        orderRepository.save(order);

        // when & then
        assertThatThrownBy(() -> orderService.cancelOrder(member2.getId(), order.getId()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage(ORDER_ACCESS_DENIED.getMessage());
    }

    @DisplayName("검색 조건에 해당하는 주문과 페이징 정보를 조회한다.")
    @Test
    void searchOrders() {
        // given
        Member member = createMember(MEMBER_NAME);
        memberRepository.save(member);

        Product product1 = createProduct(PRODUCT_STOCK_QUANTITY);
        Product product2 = createProduct(PRODUCT_STOCK_QUANTITY);
        productRepository.saveAll(List.of(product1, product2));

        OrderProduct orderProduct1 = createOrderProduct(product1, MINIMUM_ORDER_QUANTITY);
        OrderProduct orderProduct2 = createOrderProduct(product2, MINIMUM_ORDER_QUANTITY);

        Order order1 = createOrder(member, ORDERED_AT, List.of(orderProduct1));
        Order order2 = createOrder(member, ORDERED_AT.plus(ONE_SECOND_IN_MILLIS, MILLIS), List.of(orderProduct2));
        orderRepository.saveAll(List.of(order1, order2));

        OrderServiceSearchCondition searchCondition = OrderServiceSearchCondition.builder()
                .memberName(MEMBER_NAME)
                .orderStatus(INIT)
                .build();

        // when
        PageResponse<OrderResponse> response = orderService.searchOrders(searchCondition, DEFAULT_PAGE_REQUEST);

        // then
        Long order1Id = order1.getId();
        Long order2Id = order2.getId();
        List<OrderResponse> content = response.getContent();

        assertThat(content).hasSize(EXPECTED_SIZE_2)
                .extracting(
                        OrderResponse::getOrderId,
                        OrderResponse::getMemberName,
                        OrderResponse::getOrderedAt,
                        OrderResponse::getOrderStatus,
                        OrderResponse::getTotalPrice
                )
                .containsExactly(
                        tuple(
                                order1Id,
                                MEMBER_NAME,
                                ORDERED_AT,
                                INIT.name(),
                                calculateTotalPrice(content, order1Id)
                        ),
                        tuple(
                                order2Id,
                                MEMBER_NAME,
                                ORDERED_AT.plus(ONE_SECOND_IN_MILLIS, MILLIS),
                                INIT.name(),
                                calculateTotalPrice(content, order2Id)
                        )
                );

        assertThat(response)
                .extracting(
                        PageResponse::getSize,
                        PageResponse::getNumber,
                        PageResponse::getTotalElements,
                        PageResponse::getTotalPages
                )
                .containsExactly(
                        2, 0, 2L, 1
                );
    }

    @DisplayName("검색 조건에 해당하는 주문이 없을 경우, 빈 페이지를 반환한다.")
    @Test
    void searchOrders_NoContent() {
        // given
        OrderServiceSearchCondition searchCondition = OrderServiceSearchCondition.builder()
                .memberName(MEMBER_NAME)
                .orderStatus(INIT)
                .build();

        // when
        PageResponse<OrderResponse> response = orderService.searchOrders(searchCondition, DEFAULT_PAGE_REQUEST);

        // then
        assertThat(response.getContent()).isEmpty();
        assertThat(response)
                .extracting(
                        PageResponse::getSize,
                        PageResponse::getNumber,
                        PageResponse::getTotalElements,
                        PageResponse::getTotalPages
                )
                .containsExactly(
                        2, 0, 0L, 0
                );
    }

    private Member createMember() {
        return createMember(MEMBER_NAME);
    }

    private Member createMember(String memberName) {
        return Member.builder()
                .oauthId(OAUTH_ID)
                .name(memberName)
                .email(MEMBER_EMAIL)
                .role(USER)
                .oauthProvider(KAKAO)
                .build();
    }

    private Product createProduct(int stockQuantity) {
        return createProduct(PRODUCT_NAME, PRODUCT_PRICE, stockQuantity);
    }

    private Product createProduct(String name, int price, int stockQuantity) {
        return Product.builder()
                .name(name)
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
        return Order.builder()
                .member(member)
                .orderStatus(INIT)
                .orderedAt(orderedAt)
                .orderProducts(orderProducts)
                .build();
    }

    private int calculateTotalPrice(List<OrderResponse> orderResponses, Long orderId) {
        return orderResponses.stream()
                .filter(or -> or.getOrderId().equals(orderId))
                .flatMapToInt(or -> or.getOrderProducts().stream()
                        .mapToInt(opr -> opr.getOrderPrice() * opr.getOrderQuantity()))
                .sum();
    }
}
