package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin/report")
@Slf4j
@Tag(name = "数据统计接口")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/turnoverStatistics")
    @Operation(summary = "营业额统计")
    public Result<TurnoverReportVO> turnoverStatistics(
            @RequestParam("begin")
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @RequestParam("end")
            @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate end
    ){
        log.info("营业额统计：{}到{}", begin, end);
        return Result.success(reportService.getTurnoverStatistics(begin, end));
    }


    @GetMapping("/userStatistics")
    @Operation(summary = "用户统计")
    public Result<UserReportVO> userStatistics(
            @RequestParam("begin")
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @RequestParam("end")
            @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate end
    ){
        log.info("用户统计：{}到{}", begin, end);
        return Result.success(reportService.getUserStatistics(begin, end));
    }

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/ordersStatistics")
    @Operation(summary = "订单统计")
    public Result<OrderReportVO> ordersStatistics(
            @RequestParam("begin")
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @RequestParam("end")
            @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate end
    ){
        log.info("订单统计：{}到{}", begin, end);
        return Result.success(reportService.getOrdersStatistics(begin, end));
    }


    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/top10")
    @Operation(summary = "销量排名top10")
    public Result<SalesTop10ReportVO> top10(
            @RequestParam("begin")
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @RequestParam("end")
            @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate end
    ){
        log.info("销量排名top10：{}到{}", begin, end);
        return Result.success(reportService.getSalesTop10(begin, end));
    }


    @GetMapping("/export")
    @Operation(summary = "导出数据")
    public void export(HttpServletResponse response){
        reportService.exportBusinessData(response);
    }




}
