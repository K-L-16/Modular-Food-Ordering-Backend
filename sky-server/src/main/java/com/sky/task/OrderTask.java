package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理超时订单任务，每分钟执行一次
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeoutOrder(){
        log.info("处理超时订单.{}", LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        List<Orders> orders = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, time);

        if(orders != null && orders.size() > 0){
            for (Orders order : orders) {
                order.setStatus(Orders.CANCELLED);
                order.setCancelReason("订单超时，自动取消");
                order.setCancelTime(LocalDateTime.now());
                orderMapper.update(order);

            }

        }

    }


    /**
     * 处理派送订单任务，每天凌晨1点执行一次
     */
    @Scheduled(cron = "0 0 1 * * ?") //每天凌晨1点执行一次
    public void processDeliveryOrder(){
        log.info("处理派送中的订单.{}", LocalDateTime.now());

        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);

        List<Orders> orders = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, time);

        if(orders != null && orders.size() > 0){
            for (Orders order : orders) {
                order.setStatus(Orders.COMPLETED);
                order.setCancelTime(LocalDateTime.now());
                orderMapper.update(order);
            }
        }

    }
}
