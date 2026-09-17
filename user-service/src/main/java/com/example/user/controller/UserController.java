package com.example.user.controller;

import com.example.common.entity.User;
import com.example.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Value("${server.port}")
    private String serverPort;

    /**
     * 根据 ID 查询用户
     */
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        log.info("user-service [{}] 收到请求: 查询用户 {}", serverPort, id);
        // 模拟数据库查询
        User user = new User(id, "用户" + id, "user" + id + "@example.com");
        return Result.success(user);
    }

    /**
     * 查询所有用户
     */
    @GetMapping("/list")
    public Result<List<User>> listUsers() {
        log.info("user-service [{}] 收到请求: 查询用户列表", serverPort);
        List<User> users = new ArrayList<>();
        users.add(new User(1L, "张三", "zhangsan@example.com"));
        users.add(new User(2L, "李四", "lisi@example.com"));
        users.add(new User(3L, "王五", "wangwu@example.com"));
        return Result.success(users);
    }
}
