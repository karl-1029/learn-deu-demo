package com.example.order.service;

import com.example.common.entity.Order;
import com.example.common.entity.User;
import com.example.common.result.Result;
import com.example.order.feign.UserFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 订单服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserFeignClient userFeignClient;

    /**
     * 创建订单（演示跨服务调用）
     */
    public Result<Order> createOrder(Long userId, BigDecimal amount) {
        // 1. 调用用户服务查询用户信息
        Result<User> userResult = userFeignClient.getUserById(userId);
        if (userResult.getCode() != 200) {
            return Result.fail("创建订单失败: " + userResult.getMessage());
        }

        User user = userResult.getData();
        log.info("查询到用户信息: {}", user);

        // 2. 模拟创建订单
        Order order = new Order(
                1L,
                UUID.randomUUID().toString().replace("-", "").substring(0, 16),
                userId,
                amount,
                "CREATED"
        );

        log.info("订单创建成功: {}", order);
        return Result.success(order);
    }

    /**
     * 查询订单列表
     */
    public Result<List<Order>> listOrders() {
        List<Order> orders = new ArrayList<>();
        orders.add(new Order(1L, "ORD20240001", 1L, new BigDecimal("99.90"), "PAID"));
        orders.add(new Order(2L, "ORD20240002", 2L, new BigDecimal("199.00"), "CREATED"));
        orders.add(new Order(3L, "ORD20240003", 1L, new BigDecimal("59.90"), "SHIPPED"));
        return Result.success(orders);
    }
}
