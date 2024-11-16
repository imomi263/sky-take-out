package com.sky.service.Impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import io.netty.util.internal.StringUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;


@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private WorkspaceService workspaceService;

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

    @Override
    public SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime=LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime=LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> goodsSalesDTOList=orderMapper.getSalesTop10(beginTime,endTime);

        List<String> names = goodsSalesDTOList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());

        List<Integer> numbers = goodsSalesDTOList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());

        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(names,","))
                .numberList(StringUtils.join(numbers,","))
                .build();

    }

    @Override
    public void exportBusinessData(HttpServletResponse response) throws IOException {
        // 查询数据库，获取近30天营业数据
        LocalDate dateBegin = LocalDate.now().minusDays(30);

        LocalDate dateEnd = LocalDate.now().minusDays(1);


        BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(dateBegin, LocalTime.MIN),
                LocalDateTime.of(dateEnd, LocalTime.MAX));

        // 从类路径下读取资源，返回输入流对象

        // 基于模板文件创建一个新的Excel文件
        XSSFWorkbook excelWorkbook = new XSSFWorkbook(
                this.getClass().getClassLoader().
                        getResourceAsStream("template/template.xlsx")
        );

        // 获取表格的标签页

        XSSFSheet sheet1 = excelWorkbook.getSheet("Sheet1");


        // 填充数据时间段
        sheet1.getRow(1).getCell(1)
                .setCellValue("时间"+dateBegin+"至"+dateEnd);

        // 获得第4行
        XSSFRow row = sheet1.getRow(3);
        row.getCell(2).setCellValue(businessData.getTurnover());
        row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
        row.getCell(6).setCellValue(businessData.getNewUsers());


        row = sheet1.getRow(4);
        row.getCell(2).setCellValue(businessData.getValidOrderCount());
        row.getCell(4).setCellValue(businessData.getUnitPrice());


        // 填充明细数据
        for(int i=0;i<30;i++){
            LocalDate date=dateBegin.plusDays(i);
            businessData = workspaceService.getBusinessData(
                    LocalDateTime.of(date, LocalTime.MIN)
                    , LocalDateTime.of(date, LocalTime.MAX)
            );

            row=sheet1.getRow(7+i);
            row.getCell(1).setCellValue(date.toString());
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(3).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(5).setCellValue(businessData.getUnitPrice());
            row.getCell(6).setCellValue(businessData.getNewUsers());
        }

        // 通过输出流将Excel文件下载到浏览器
        ServletOutputStream outputStream = response.getOutputStream();
        excelWorkbook.write(outputStream);


        // 调试的时候把下载的文件后缀重命名为xlsx就可以正常打开了
        outputStream.close();
        excelWorkbook.close();


    }


}
