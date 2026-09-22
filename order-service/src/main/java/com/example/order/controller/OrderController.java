package com.example.order.controller;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.example.common.entity.Order;
import com.example.common.result.Result;
import com.example.order.config.CustomerConfigProperties;
import com.example.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单控制器
 */
@Slf4j
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

    /**
     * Sentinel 演示：流控规则，资源名和 FlowRuleConfig 保持一致。
     * QPS 超过 2 时，将触发 BlockException，返回限流结果。
     */
    @GetMapping("/sentinel/demo")
    public Result<String> sentinelDemo() {
        try (Entry entry = SphU.entry("order-sentinel-demo")) {
            log.info("order-service 执行业务逻辑，进入 Sentinel 保护的资源：order-sentinel-demo");
            return Result.success("Sentinel demo 调用成功，业务正常执行");
        } catch (BlockException e) {
            log.warn("Sentinel 限流触发：{}", e.getRuleLimitApp(), e);
            return Result.fail("Sentinel 限流触发，稍后再试");
        }
    }

    /**
     * 用于演示在运行时清理规则，以便再测一次。
     */
    @PostMapping("/sentinel/reset")
    public Result<String> clearSentinelRules() {
        FlowRuleManager.loadRules(Collections.emptyList());
        return Result.success("Sentinel 规则已清空，当前不再限流");
    }
}
