package com.example.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 全局过滤器 — 记录所有经过网关的请求日志和耗时
 * <p>
 * 这个类不依赖 yml 配置，原因是：
 * 1. 它实现的是 GlobalFilter
 * 2. 它带有 @Component
 * 3. Spring 会自动把它注册到 Gateway 的全局过滤器链中
 */
@Slf4j
@Component
public class RequestLogGlobalFilter implements GlobalFilter, Ordered {

    private static final String START_TIME_ATTR = "requestStartTime";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        String method = request.getMethod().name();

        // 记录请求开始时间（存入 exchange 的 attributes 中，供响应阶段使用）
        exchange.getAttributes().put(START_TIME_ATTR, System.currentTimeMillis());

        log.info("[Gateway] 请求进入: {} {} from {}", method, path);

        // 放行：chain.filter 之前是"请求阶段"，then 之后是"响应阶段"
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            Long startTime = exchange.getAttribute(START_TIME_ATTR);
            if (startTime != null) {
                long duration = System.currentTimeMillis() - startTime;
                log.info("[Gateway] 请求完成: {} {} 耗时: {}ms, 状态码: {}",
                        method, path, duration,
                        exchange.getResponse().getStatusCode());
            }
        }));
    }

    /**
     * 过滤器执行顺序，数值越小优先级越高
     */
    @Override
    public int getOrder() {
        return 1;
    }
}
