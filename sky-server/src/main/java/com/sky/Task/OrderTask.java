package com.sky.Task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {

    @Resource
    private OrderMapper orderMapper;



    // 处理超时订单方法
    @Scheduled(cron="0 * * * * *")
    public void processTimeoutOrder(){

        log.info("定时处理超时订单:{}", LocalDateTime.now());

        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);

        // 查找所有的待付款order并且下单时间超过了15分钟
        List<Orders> byStatusAndOrderTimeLT = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, time);

        if(byStatusAndOrderTimeLT!=null &&byStatusAndOrderTimeLT.size() > 0){
            for(Orders order : byStatusAndOrderTimeLT){
                order.setStatus(Orders.CANCELLED);
                order.setCancelReason("订单超时自动取消");
                order.setCancelTime(LocalDateTime.now());
                orderMapper.update(order);
            }

        }
    }

    // 一直处于派送中订单，自动完成
    @Scheduled(cron="0 0 1 * * ?") // 每天凌晨一点
    public void processDeliveryOrder(){
        log.info("定时处理处于派送中的订单:{}", LocalDateTime.now());
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(
                Orders.DELIVERY_IN_PROGRESS, LocalDateTime.now().minusMinutes(60)
        );

        if(ordersList!=null && ordersList.size()>0){
            for(Orders order : ordersList){
                order.setStatus(Orders.COMPLETED);
                orderMapper.update(order);
            }
        }

    }
}
