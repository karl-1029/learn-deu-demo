package com.example.order.controller;

import com.example.common.entity.Order;
import com.example.common.result.Result;
import com.example.order.config.CustomerConfigProperties;
import com.example.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单控制器
 */
@RefreshScope
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private CustomerConfigProperties customerConfigProperties;

    /**
     * @Value 注入的配置项（支持通过 Nacos 动态刷新）
     */
    @Value("${order-service.config.description}")
    private String descriptionFromValue;

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

    /**
     * Nacos 配置中心示例接口
     * <p>
     * 演示两种读取方式：
     * 1. @ConfigurationProperties 绑定（NacosConfigProperties）
     * 2. @Value 注入（支持 @RefreshScope 动态刷新）
     * <p>
     * 在 Nacos 控制台修改 order-service.yaml 配置后，再次调用此接口即可看到最新值，无需重启服务。
     */
    @GetMapping("/config")
    public Result<Map<String, String>> getNacosConfig() {
        Map<String, String> config = new HashMap<>();
        // 方式一：@ConfigurationProperties
        config.put("appName(配置类)", customerConfigProperties.getAppName());
        config.put("env(配置类)", customerConfigProperties.getEnv());
        config.put("description(配置类)", customerConfigProperties.getDescription());
        // 方式二：@Value + @RefreshScope
        config.put("description(@Value)", descriptionFromValue);
        return Result.success(config);
    }
}
