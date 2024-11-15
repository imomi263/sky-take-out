package com.sky.service.Impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sky.WebSocket.WebSocketServer;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrderPaymentDTO;
import com.sky.dto.OrderSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import jakarta.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

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

    @Resource
    private UserMapper userMapper;

    @Resource
    private WeChatPayUtil weChatPayUtil;

    @Resource
    private WebSocketServer webSocketServer;


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


    @Override
    public OrderPaymentVO payment(OrderPaymentDTO orderPaymentDTO) throws Exception {
        Long userId=BaseContext.getCurrentId();
        User user=userMapper.getById(userId);

        JSONObject jsonObject=weChatPayUtil.pay(
                orderPaymentDTO.getOrderNumber(),
                new BigDecimal(0.01),
                "苍穹外卖订单",
                user.getOpenid()
        );

        if(jsonObject.getString("code")!=null&&jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO orderPaymentVO=jsonObject.toJavaObject(OrderPaymentVO.class);
        orderPaymentVO.setPackageStr(jsonObject.getString("package"));
        return orderPaymentVO;
    }

    @Override
    public void paySuccess(String outTradeNo) {
        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);

        // 通过webSocket向客户端浏览器推送消息 type orderId content
        Map map=new HashMap();
        map.put("type",1);  // 1表示来单提醒，2表示客户催单
        map.put("orderId",ordersDB.getId());
        map.put("content","订单号" + outTradeNo);
        String json=JSON.toJSONString(map);

        webSocketServer.sendToAllClient(json);
    }

    @Override
    public void reminder(Long id) {
        Orders ordersDB=orderMapper.getById(id);

        if(ordersDB==null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        Map map=new HashMap();
        map.put("type",2);
        map.put("orderId",id);
        map.put("content","订单号"+ordersDB.getNumber());

        // 通过websocket推送到浏览器
        webSocketServer.sendToAllClient(JSON.toJSONString(map));

    }
}
