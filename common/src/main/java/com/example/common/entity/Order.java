package com.example.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order implements Serializable {

    private Long id;
    private String orderNo;
    private Long userId;
    private BigDecimal amount;
    private String status;
}
