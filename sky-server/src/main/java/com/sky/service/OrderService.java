package com.sky.service;

import com.sky.dto.OrderPaymentDTO;
import com.sky.dto.OrderSubmitDTO;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;

public interface OrderService {
    OrderSubmitVO submitOrder(OrderSubmitDTO orderSubmitDTO);

    OrderPaymentVO payment(OrderPaymentDTO orderPaymentDTO) throws Exception;

    void paySuccess(String outTradeNo);
}
