package com.example.gateway.filter;

import io.micrometer.tracing.Tracer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
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
public class AddTraceIdGatewayFilterFactory
        extends AbstractGatewayFilterFactory<AddTraceIdGatewayFilterFactory.Config> {


    public AddTraceIdGatewayFilterFactory(Tracer tracer) {
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
        String headerName = Optional.ofNullable(config.getName()).filter(n -> !n.isBlank()).orElse("X-Trace-Source");
        String headerValue = Optional.ofNullable(config.getValue()).filter(v -> !v.isBlank()).orElse("gateway");

        log.info("[AddTraceId GatewayFilterFactory 路由级别的filter] 过滤器已注册, key={}, value={}", headerName, headerValue);
        return (exchange, chain) -> {

            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header(headerName, headerValue)
                    .build();

            log.info("[AddTraceId GatewayFilterFactory 路由级别的filter] 注入 , {}={}", headerName, headerValue);
            return chain.filter(exchange.mutate().request(mutatedRequest).build())
                    .doFinally(signalType -> {
                        MDC.remove("traceId");
                        MDC.remove("spanId");
                    });
        };
    }

    @Data
    public static class Config {
        private String name;
        private String value;
    }
}
