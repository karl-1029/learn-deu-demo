package com.example.order.feign;

import com.example.common.entity.User;
import com.example.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 用户服务 Feign 客户端
 */
@FeignClient(name = "user-service", fallbackFactory = UserFeignFallbackFactory.class)
public interface UserFeignClient {

    @GetMapping("/user/{id}")
    Result<User> getUserById(@PathVariable("id") Long id);
}
