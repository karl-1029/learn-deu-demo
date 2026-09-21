package com.example.gateway.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 自定义路由级过滤器工厂 — 向请求头中注入 traceId
 * <p>
 * 配置示例：
 * <pre>
 * filters:
 *   - AddTraceId=X-Trace-Source,gateway
 * </pre>
 * 这里会被解析成：name = "X-Trace-Source"，value = "gateway"
 */
@Slf4j
@Component
public class AddTraceIdGatewayFilterFactory
        extends AbstractGatewayFilterFactory<AddTraceIdGatewayFilterFactory.Config> {

    public AddTraceIdGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public String name() {
        return "AddTraceId";
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("name", "value");
    }

    @Override
    public GatewayFilter apply(Config config) {
        log.info("[AddTraceId] 过滤器已注册, key={}, value={}", config.getName(), config.getValue());
        return (exchange, chain) -> {
            String traceId = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 16);

            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-Trace-Id", traceId)
                    .header(config.getName(), config.getValue())
                    .build();

            log.info("[AddTraceId] 注入 traceId={}, {}={}", traceId, config.getName(), config.getValue());
            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        };
    }

    @Data
    public static class Config {
        private String name;
        private String value;
    }
}
