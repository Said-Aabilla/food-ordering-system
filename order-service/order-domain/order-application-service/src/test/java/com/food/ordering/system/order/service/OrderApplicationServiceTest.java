package com.food.ordering.system.order.service;

import com.food.ordering.system.domain.entity.Customer;
import com.food.ordering.system.domain.entity.Order;
import com.food.ordering.system.domain.entity.Product;
import com.food.ordering.system.domain.entity.Restaurant;
import com.food.ordering.system.domain.valueobject.*;
import com.food.ordering.system.order.service.domain.OrderCreateCommandHandler;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderAddress;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderItem;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.ports.input.service.OrderApplicationService;
import com.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = OrderTestConfiguration.class)
public class OrderApplicationServiceTest {

    @Autowired
    private OrderApplicationService orderApplicationService;

    @Autowired
    private OrderDataMapper orderDataMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    private CreateOrderCommand createOrderCommand;

    private CreateOrderCommand createOrderCommandWrongPrice;
    private CreateOrderCommand createOrderCommandWrongProductPrice;

    private final UUID CUSTOMER_ID = UUID.fromString("1e9f6bb9-1f32-49d2-9df0-bc6616f39785");
    private final UUID RESTAURANT_ID = UUID.fromString("2e5f6bb9-1f32-49d2-9df0-bc6616f39785");
    private final UUID PRODUCT_ID = UUID.fromString("3e9f6bb9-1f32-49d2-9df0-bc6616f39785");
    private final UUID ORDER_ID = UUID.fromString("4ef6bb9-1f32-49d2-9df0-bc6616f39785");
    private final BigDecimal PRICE = BigDecimal.valueOf(200.00);


    @BeforeAll
    public void init() {

        createOrderCommand = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .createOrderAddress(CreateOrderAddress.builder()
                        .city("Rabat")
                        .postalCode("40000")
                        .street("Hay Ryad")
                        .build())
                .price(PRICE)
                .createOrderItems(List.of(
                        CreateOrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(1)
                                .subTotal(BigDecimal.valueOf(50.00))
                                .price(BigDecimal.valueOf(50.00))
                                .build(),
                        CreateOrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(3)
                                .subTotal(BigDecimal.valueOf(150.00))
                                .price(BigDecimal.valueOf(50.00))
                                .build()
                ))
                .build();

        createOrderCommandWrongPrice = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .createOrderAddress(CreateOrderAddress.builder()
                        .city("Rabat")
                        .postalCode("40000")
                        .street("Hay Ryad")
                        .build())
                .price(BigDecimal.valueOf(250.00))
                .createOrderItems(List.of(
                        CreateOrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(1)
                                .subTotal(BigDecimal.valueOf(50.00))
                                .price(BigDecimal.valueOf(50.00))
                                .build(),
                        CreateOrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(3)
                                .subTotal(BigDecimal.valueOf(150.00))
                                .price(BigDecimal.valueOf(50.00))
                                .build()
                ))
                .build();

        createOrderCommand = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .createOrderAddress(CreateOrderAddress.builder()
                        .city("Rabat")
                        .postalCode("40000")
                        .street("Hay Ryad")
                        .build())
                .price(BigDecimal.valueOf(210.00))
                .createOrderItems(List.of(
                        CreateOrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(1)
                                .subTotal(BigDecimal.valueOf(60.00))
                                .price(BigDecimal.valueOf(60.00))
                                .build(),
                        CreateOrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(3)
                                .subTotal(BigDecimal.valueOf(150.00))
                                .price(BigDecimal.valueOf(50.00))
                                .build()
                ))
                .build();

        Customer customer = new Customer();
        customer.setId(new CustomerId(CUSTOMER_ID));

        Restaurant restaurant = Restaurant.builder()
                .restaurantId(new RestaurantId(RESTAURANT_ID))
                .products(List.of(
                        new Product(new ProductId(PRODUCT_ID), "product-1", new Money(BigDecimal.valueOf(50.00))),
                        new Product(new ProductId(PRODUCT_ID), "product-2", new Money(BigDecimal.valueOf(50.00)))
                ))
                .active(true)
                .build();
        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        order.setId(new OrderId(UUID.randomUUID()));

        when(customerRepository.findCustomer(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(restaurantRepository.findRestaurantInformation(
                orderDataMapper.createOrderCommandToRestaurant(createOrderCommand))
        ).thenReturn(Optional.of(restaurant));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
    }

}

