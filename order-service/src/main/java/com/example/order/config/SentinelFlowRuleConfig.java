package com.example.order.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SentinelFlowRuleConfig {

    /**
     * 使用 ApplicationRunner 而非 InitializingBean，
     * 确保规则加载在所有 Bean（包括 Sentinel transport）初始化完成之后执行，
     * 避免提前触发 InitExecutor.doInit() 导致 HeartbeatSender 读不到 dashboard 地址。
     */

    /**
     * 规则已通过 application.yml 中的 spring.cloud.sentinel.datasource 从 Nacos 加载，
     * 无需再硬编码。FlowRuleManager.loadRules() 是全量覆盖，会与 Nacos 数据源冲突。
     */
//    @Bean
//    public ApplicationRunner sentinelFlowRulesInitializer() {
//        return (ApplicationArguments args) -> {
//            List<FlowRule> rules = new ArrayList<>();
//
//            FlowRule flowRule = new FlowRule();
//            flowRule.setResource("order-sentinel-demo");
//            flowRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
//            flowRule.setCount(3);
//            flowRule.setLimitApp("default");
//            rules.add(flowRule);
//
//            FlowRuleManager.loadRules(rules);
//        };
//    }
}
