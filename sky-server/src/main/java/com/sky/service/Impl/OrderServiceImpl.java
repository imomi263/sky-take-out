package com.sky.service.Impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrderSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import jakarta.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderDetailMapper orderdetailMapper;


    @Resource
    private AddressBookMapper addressBookMapper;

    @Resource
    private ShoppingCartMapper shoppingCartMapper;


    @Override
    @Transactional()
    public OrderSubmitVO submitOrder(OrderSubmitDTO orderSubmitDTO) {

        // 处理各种业务异常（地址为空）
        AddressBook addressbook=addressBookMapper.getById(orderSubmitDTO.getAddressId());
        if(addressbook==null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }




        // 向订单表插入1条数据,且需要返回主键值
        Long userId= BaseContext.getCurrentId();
        ShoppingCart shoppingCart=new ShoppingCart();
        shoppingCart.setUserId(userId);


        List<ShoppingCart> shoppingCarts = shoppingCartMapper.listShoppingCart(shoppingCart);
        if(shoppingCarts==null || shoppingCarts.size()==0){
            throw new ShoppingCartBusinessException(MessageConstant.CART_IS_NULL);
        }



        Orders orders=new  Orders();
        BeanUtils.copyProperties(orderSubmitDTO,orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setPayStatus(1);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setConsignee(addressbook.getConsignee());
        orders.setPhone(addressbook.getPhone());
        orders.setUserId(userId);


        orderMapper.insert(orders);


        // 向订单明细表插入n条数据
        List<OrderDetail> orderDetailList=new ArrayList<>();
        for(ShoppingCart cart:shoppingCarts){
            OrderDetail orderDetail=new OrderDetail();
            BeanUtils.copyProperties(cart,orderDetail);
            orderDetail.setOrderId(orders.getId()); // 设置订单ID
            orderDetailList.add(orderDetail);
        }

        orderdetailMapper.insertBatch(orderDetailList);


        // 清空当前用户的购物车数据

        shoppingCartMapper.deleteByUserId(userId);

        // 封装VO返回

        OrderSubmitVO orderSubmitVO= OrderSubmitVO.builder()
                .id(userId)
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .build();

        return orderSubmitVO;
    }
}
