package com.sky.service.Impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.netty.util.internal.StringUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private UserMapper userMapper;

    /**
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList=new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)) {
            begin=begin.plusDays(1);
            dateList.add(begin);
        }
        List<Double> turnOverList=new ArrayList<>();
        for(LocalDate date : dateList) {
            // 查询该日的营业额，查询已完成的订单
            LocalDateTime beginTime=LocalDateTime.of(date, LocalTime.MIN);
            // select sum(amount) from orders where order_time > ? and
            // order_time < ? and status= 5

            LocalDateTime endTime=LocalDateTime.of(date, LocalTime.MAX);
            Map map=new HashMap();
            map.put("beginTime",beginTime);
            map.put("endTime",endTime);
            map.put("status", Orders.COMPLETED);

            Double turnover=orderMapper.sumByMap(map);
            turnover=turnover==null ? 0.0 : turnover;
            turnOverList.add(turnover);
        }

        return TurnoverReportVO
                .builder()
                .turnoverList(StringUtils.join(turnOverList, ","))
                .dateList(StringUtils.join(dateList,","))
                .build();
    }

    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList=new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)) {
            begin=begin.plusDays(1);
            dateList.add(begin);
        }

        List<Integer> newUserList=new ArrayList<>();
        List<Integer> totalUserList=new ArrayList<>();
        for(LocalDate date : dateList) {
            LocalDateTime beginTime=LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime=LocalDateTime.of(date, LocalTime.MAX);

            Map map=new HashMap();


            map.put("end",endTime);
            Integer totalUser=userMapper.countByMap(map);

            map.put("begin",beginTime);
            Integer newUser=userMapper.countByMap(map);

            if(totalUser==null){
                totalUser=0;
            }
            if(newUser==null){
                newUser=0;
            }
            newUserList.add(newUser);
            totalUserList.add(totalUser);

        }

        return UserReportVO.builder()
                .newUserList(StringUtils.join(newUserList,","))
                .totalUserList(StringUtils.join(totalUserList,","))
                .build();
    }

    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList=new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)) {
            begin=begin.plusDays(1);
            dateList.add(begin);
        }

        // 遍历日期，查看当日的订单
        List<Integer> orderListCount=new ArrayList<>();
        List<Integer> validOrderListCount=new ArrayList<>();
        for(LocalDate date : dateList) {

            LocalDateTime beginTime=LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime=LocalDateTime.of(date, LocalTime.MAX);
            Map map=new HashMap();
            map.put("begin",beginTime);
            map.put("end",endTime);

            Integer orderCount=orderMapper.countByMap(map);

            map.put("status", Orders.COMPLETED);

            Integer validCount=orderMapper.countByMap(map);

            if(orderCount==null){
                orderCount=0;
            }
            if(validCount==null){
                validCount=0;
            }

            orderListCount.add(orderCount);
            validOrderListCount.add(validCount);
        }

        Integer totalOrderCount=orderListCount.stream().reduce(Integer::sum).get();
        Integer totalValidOrderCount=validOrderListCount.stream().reduce(Integer::sum).get();

        Double orderCompletionRate=0.0;
        if(totalOrderCount==0){
            return null;
        }
        orderCompletionRate=totalValidOrderCount.doubleValue()/totalOrderCount;

        return OrderReportVO.builder()
                .totalOrderCount(totalOrderCount)
                .orderCountList(StringUtils.join(orderListCount,","))
                .validOrderCountList(StringUtils.join(validOrderListCount,","))
                .validOrderCount(totalValidOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();

    }


}
