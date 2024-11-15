package com.sky.service.Impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
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


}