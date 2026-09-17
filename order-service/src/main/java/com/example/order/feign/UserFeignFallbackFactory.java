package com.example.order.feign;

import com.example.common.entity.User;
import com.example.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * UserFeignClient 降级工厂
 */
@Slf4j
@Component
public class UserFeignFallbackFactory implements FallbackFactory<UserFeignClient> {

    @Override
    public UserFeignClient create(Throwable cause) {
        log.error("用户服务调用失败，触发降级: {}", cause.getMessage());
        return new UserFeignClient() {
            @Override
            public Result<User> getUserById(Long id) {
                return Result.fail("用户服务暂时不可用，请稍后重试");
            }
        };
    }
}
