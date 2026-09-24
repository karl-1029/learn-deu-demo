package com.example.user.controller;

import com.example.common.entity.User;
import com.example.common.result.Result;
import com.example.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;


    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable("id") Long id) {
        User user = new User();
        user.setId(id);
        user.setUsername("test_" + id);
        return Result.success(user);
    }

    @PostMapping("/create")
    public Result<Void> create() {
        User user = new User();
        user.setUsername("createUser" + System.currentTimeMillis());
        userMapper.insert(user);
        return Result.success(null);
    }
}
