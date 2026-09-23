package com.example.order.controller;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.example.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@Slf4j
@RestController
@RequestMapping("/order")
public class SentinelDemoController {

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
     * Sentinel 注解式资源演示。
     * 当 QPS 超过规则限制时，会进入 blockHandler 指定的方法。
     * 当方法内部抛出异常时，会进入 fallback 指定的方法。
     */
    @GetMapping("/sentinel/annotated")
    @SentinelResource(value = "order-sentinel-demo-anno", blockHandler = "sentinelAnnotatedBlockHandler", fallback = "sentinelAnnotatedFallback")
    public Result<String> sentinelDemoAnnotated(String id) {
        log.info("order-service 注解式资源被调用：order-sentinel-demo-anno id : {}" ,id);
        // 模拟业务异常演示 fallback（可注释掉以测试限流）
        if (StringUtils.isEmpty(id)) {
            throw new RuntimeException("模拟业务异常 走 fallback降级");
        }
        return Result.success("Sentinel 注解 demo 调用成功，业务正常执行");
    }

    // blockHandler 方法签名：与原方法参数一致，最后加 BlockException 参数
    public Result<String> sentinelAnnotatedBlockHandler(String id, BlockException ex) {
        log.warn("注解式 Sentinel 限流触发，id={}：{}", id, ex.getClass().getSimpleName());
        return Result.fail("注解式 Sentinel 限流，请稍后再试");
    }

    // fallback 方法签名：与原方法参数一致，末尾可加 Throwable 参数
    public Result<String> sentinelAnnotatedFallback(String id, Throwable ex) {
        log.error("注解式 Sentinel 触发降级或异常，id={}：", id, ex);
        return Result.fail("注解式 Sentinel 降级，错误：" + ex.getMessage());
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
