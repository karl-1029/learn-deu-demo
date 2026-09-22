package com.example.order.controller;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.example.common.result.Result;
import lombok.extern.slf4j.Slf4j;
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
     * 用于演示在运行时清理规则，以便再测一次。
     */
    @PostMapping("/sentinel/reset")
    public Result<String> clearSentinelRules() {
        FlowRuleManager.loadRules(Collections.emptyList());
        return Result.success("Sentinel 规则已清空，当前不再限流");
    }
}
