package com.example.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    private Long id;
    private String username;
    private String email;
}
