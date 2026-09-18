package com.example.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Nacos 配置中心示例 - 通过 @ConfigurationProperties 绑定配置
 * <p>
 * 对应 Nacos 配置（Data ID: order-service.yaml）中的示例：
 * <pre>
 * nacos:
 *   config:
 *     appName: 订单服务
 *     env: dev
 *     description: 这是来自Nacos配置中心的配置
 * </pre>
 */
@Data
@Component
@ConfigurationProperties(prefix = "order-service.config")
public class CustomerConfigProperties {

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 环境标识
     */
    private String env;

    /**
     * 描述信息
     */
    private String description;
}
