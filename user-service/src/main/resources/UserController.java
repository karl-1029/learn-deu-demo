package com.example.user.controller;

import com.example.common.entity.User;
import com.example.common.result.Result;
import com.example.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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

    /**
     * Seata: 扣减用户余额接口
     * 该接口应当使用 DataSourceProxy（已在配置中启用），以便 Seata RM 能注册分支事务
     */
    @PostMapping("/decreaseBalance")
    @Transactional
    public Result<Void> decreaseBalance(@RequestParam Long userId, @RequestParam BigDecimal amount) {
        log.info("user-service 扣减余额 userId={} amount={}", userId, amount);
        userRepository.decreaseBalance(userId, amount);
        return Result.success(null);
    }
}
