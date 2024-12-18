package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/admin/report")
@Tag(name="数据统计相关接口")
@Slf4j
public class ReportController {

    @Resource
    private ReportService reportService;


    @GetMapping("/turnoverStatistics")
    @Operation(summary = "营业额统计")
    public Result<TurnoverReportVO>  turnoverStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end
    )
    {
        log.info("营业额统计");
        return Result.success(reportService.getTurnoverStatistics(begin,end));

    }

    @GetMapping("/userStatistics")
    @Operation(summary = "用户统计")
    public Result<UserReportVO> userStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end
    )
    {
        log.info("用户统计");
        return Result.success(reportService.getUserStatistics(begin,end));
    }

    @GetMapping("/orderStatistics")
    @Operation(summary = "订单统计")
    public Result<OrderReportVO> orderStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end
    )
    {
        log.info("订单统计");
        return Result.success(reportService.getOrderStatistics(begin,end));
    }


    @GetMapping("/top10")
    @Operation(summary = "销量排名")
    public Result<SalesTop10ReportVO> top10(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end
    )
    {
        log.info("销量排名");
        return Result.success(reportService.getTop10(begin,end));
    }


    @GetMapping("/export")
    @Operation(summary = "导出运营数据报表")
    public void exportExcel(HttpServletResponse response) throws IOException {
        reportService.exportBusinessData(response);

    }

}
