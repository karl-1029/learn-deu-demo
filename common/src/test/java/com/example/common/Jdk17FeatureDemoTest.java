package com.example.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Jdk17FeatureDemoTest {

    @Test
    @DisplayName("record：更简洁地表达不可变数据对象")
    void recordFeature() {
        OrderSummary summary = new OrderSummary(1001L, "MacBook Pro", 12999);
        assertThat(summary.id()).isEqualTo(1001L);
        assertThat(summary.name()).isEqualTo("MacBook Pro");
        assertThat(summary.amount()).isEqualTo(12999);
        assertThat(summary.toString()).contains("MacBook Pro");
        assertThat(summary).isEqualTo(new OrderSummary(1001L, "MacBook Pro", 12999));
    }

    @Test
    @DisplayName("switch表达式：让分支逻辑更清晰")
    void switchExpressionFeature() {
        assertThat(getStatusText(OrderStatus.CREATED)).isEqualTo("待支付");
        assertThat(getStatusText(OrderStatus.PAID)).isEqualTo("已支付");
        assertThat(getStatusText(OrderStatus.SHIPPED)).isEqualTo("已发货");
        assertThat(getStatusText(OrderStatus.CANCELLED)).isEqualTo("已取消");
    }

    @Test
    @DisplayName("文本块：处理多行字符串更自然")
    void textBlockFeature() {
        String sql = """
                SELECT id, username, email
                FROM user
                WHERE status = 'ACTIVE'
                ORDER BY id
                """;

        assertThat(sql).contains("SELECT id, username, email");
        assertThat(sql).contains("FROM user");
        assertThat(sql).contains("WHERE status = 'ACTIVE'");
        assertThat(sql).contains("ORDER BY id");
    }

    @Test
    @DisplayName("instanceof 模式匹配：减少强制类型转换")
    void patternMatchingForInstanceof() {
        assertThat(describe("hello")).isEqualTo("string: HELLO");
        assertThat(describe(42)).isEqualTo("number: 42");
        assertThat(describe(true)).isEqualTo("other type");
    }

    @Test
    @DisplayName("sealed + record：限制实现类，增强类型安全")
    void sealedClassFeature() {
        PaymentChannel channel = new AlipayChannel("demo-app");

        if (channel instanceof AlipayChannel alipay) {
            assertThat(alipay.appId()).isEqualTo("demo-app");
        } else {
            throw new AssertionError("应该匹配到 AlipayChannel");
        }

        PaymentChannel wechat = new WeChatChannel("weixin-app");
        assertThat(wechat).isInstanceOf(WeChatChannel.class);
    }

    private String describe(Object value) {
        if (value instanceof String text && !text.isBlank()) {
            return "string: " + text.toUpperCase();
        }
        if (value instanceof Number number) {
            return "number: " + number;
        }
        return "other type";
    }

    private String getStatusText(OrderStatus status) {
        return switch (status) {
            case CREATED -> "待支付";
            case PAID -> "已支付";
            case SHIPPED -> "已发货";
            case CANCELLED -> "已取消";
        };
    }

    private record OrderSummary(Long id, String name, Integer amount) {
    }

    private enum OrderStatus {
        CREATED,
        PAID,
        SHIPPED,
        CANCELLED
    }

    private sealed interface PaymentChannel permits AlipayChannel, WeChatChannel {
    }

    private record AlipayChannel(String appId) implements PaymentChannel {
    }

    private record WeChatChannel(String appId) implements PaymentChannel {
    }
}
