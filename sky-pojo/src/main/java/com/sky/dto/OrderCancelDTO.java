package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderCancelDTO implements Serializable {
    private Integer Id;
    private String cancelReason;

}
