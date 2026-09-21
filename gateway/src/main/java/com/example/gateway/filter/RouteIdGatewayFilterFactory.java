package com.example.gateway.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
public class RouteIdGatewayFilterFactory
        extends AbstractGatewayFilterFactory<RouteIdGatewayFilterFactory.Config> {

    public RouteIdGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public String name() {
        return "RouteId";
    }


    @Override
    public GatewayFilter apply(Config config) {
        String headerName = Optional.ofNullable(config.getName()).filter(n -> !n.isBlank()).orElse("X-Trace-Source");
        String headerValue = Optional.ofNullable(config.getValue()).filter(v -> !v.isBlank()).orElse("gateway");

        log.info("[RouteId GatewayFilterFactory 路由级别的filter] 过滤器已注册, key={}, value={}", headerName, headerValue);
        return (exchange, chain) -> {

            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header(headerName, headerValue)
                    .build();

            log.info("[RouteId GatewayFilterFactory 路由级别的filter] 注入 , {}={}", headerName, headerValue);
            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        };
    }

    @Data
    public static class Config {
        private String name;
        private String value;
    }
}
