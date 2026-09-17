package com.example.order.controller;

import com.example.common.entity.Order;
import com.example.common.result.Result;
import com.example.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建订单
     * 示例: POST /order/create?userId=1&amount=99.9
     */
    @PostMapping("/create")
    public Result<Order> createOrder(@RequestParam Long userId, @RequestParam BigDecimal amount) {
        return orderService.createOrder(userId, amount);
    }

    /**
     * 查询订单列表
     */
    @GetMapping("/list")
    public Result<List<Order>> listOrders() {
        return orderService.listOrders();
    }
}
