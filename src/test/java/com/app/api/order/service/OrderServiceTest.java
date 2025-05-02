package com.app.api.order.service;

import com.app.api.order.dto.request.OrderCreateRequest;
import com.app.api.order.dto.response.OrderResponse;
import com.app.domain.member.entity.Member;
import com.app.domain.member.repository.MemberRepository;
import com.app.domain.order.entity.Order;
import com.app.domain.order.repository.OrderRepository;
import com.app.domain.orderProduct.repository.OrderProductRepository;
import com.app.domain.product.entity.Product;
import com.app.domain.product.repository.ProductRepository;
import com.app.global.error.exception.OutOfStockException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.app.domain.member.constant.MemberType.KAKAO;
import static com.app.domain.member.constant.Role.USER;
import static com.app.domain.order.constant.OrderStatus.INIT;
import static com.app.domain.product.constant.ProductSellingStatus.SELLING;
import static com.app.domain.product.constant.ProductType.PRODUCT_A;
import static com.app.fixture.TimeFixture.FIXED_INSTANT;
import static com.app.fixture.TimeFixture.FIXED_TIME_ZONE;
import static com.app.global.error.ErrorType.OUT_OF_STOCK;
import static org.assertj.core.api.Assertions.*;

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
        Member member = createTestMember("member");
        memberRepository.save(member);

        Product product = createTestProduct("product", 10000, 2);
        productRepository.save(product);

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
                .extracting("memberName", "orderDateTime", "orderStatus", "totalPrice")
                .containsExactly("member", orderDateTime, INIT.name(), 20000);
        assertThat(response.getOrderProducts())
                .extracting("productId", "productName", "orderPrice", "orderQuantity")
                .containsExactly(
                        tuple(productId, "product", 10000, 2)
                );

        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        assertThat(optionalOrder).isPresent();
    }

    @DisplayName("상품 재고가 주문 수량보다 적을 때 주문 등록을 시도할 경우, 예외가 발생한다.")
    @Test
    void createOrder_StockQuantityLessThanOrderQuantity() {
        // given
        Member member = createTestMember("member");
        memberRepository.save(member);

        Product product = createTestProduct("product", 10000, 1);
        productRepository.save(product);

        LocalDateTime orderDateTime = LocalDateTime.ofInstant(FIXED_INSTANT, FIXED_TIME_ZONE);

        OrderCreateRequest request = OrderCreateRequest.builder()
                .productId(product.getId())
                .orderQuantity(2)
                .build();

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(member.getId(), orderDateTime, request))
                .isInstanceOf(OutOfStockException.class)
                .hasMessage(OUT_OF_STOCK.getErrorMessage());
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

    private Member createTestMember(String name) {
        return Member.builder()
                .name(name)
                .email("member@email.com")
                .role(USER)
                .profile("profile")
                .memberType(KAKAO)
                .build();
    }
}
