package com.example.order.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SentinelFlowRuleConfig {

    @Bean
    public InitializingBean sentinelFlowRulesInitializer() {
        return () -> {
            List<FlowRule> rules = new ArrayList<>();

            FlowRule flowRule = new FlowRule();
            flowRule.setResource("order-sentinel-demo");
            flowRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
            flowRule.setCount(2);
            flowRule.setLimitApp("default");
            rules.add(flowRule);

            FlowRuleManager.loadRules(rules);
        };
    }
}
