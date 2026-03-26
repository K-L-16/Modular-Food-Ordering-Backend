package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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



    @Autowired
    private OrderMapper orderMapper;

    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        // 当前集合用于存放从begin到end的每天的日期
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);
        while (!begin.equals(end)) {
            // 日期计算，begin = begin + 1
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        // 将dateList进行日期转换
        String dateStr = StringUtils.join(dateList, ",");

        //遍历dateList
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            //查询date日期对印度个营业额数据，营业额是指，状态为已完成的订单金额合计
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN); //获得最小的时间
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX); //互动二当天的最大的时间

            //selet sum(amount) from orders where order_time >= ? and order_time < ? and status = 5
            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover =  orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add( turnover);
        }

        // 查询数据库，获取日期对应的营业额
        return TurnoverReportVO
                .builder()
                .dateList(dateStr)
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        //存放begin到end之间的每天对应的日期
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);
        while (!begin.equals(end)) {
            //日期计算，begin = begin + 1
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        //新增用户数量 select count(id) from user where create_time > ? and create_time < ?
        List<Integer> newUserList = new ArrayList<>();
        //总用户数量 select count(id) from user where  and create_time < ?
        List<Integer> totalUserList = new ArrayList<>();

        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap();
            map.put("end", endTime);

            //总用户数量
            Integer totalUser = orderMapper.countByMap(map);

            map.put("begin", beginTime);
            //新增用户数量
            Integer newUser = orderMapper.countByMap(map);

            totalUserList.add(totalUser);
            newUserList.add(newUser);
        }




        return UserReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();
    }

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
        //存放begin到end之间的每天对应的日期
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);
        while (!begin.equals(end)) {
            //日期计算，begin = begin + 1
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        //订单总数
        List<Integer> orderCountList = new ArrayList<>();
        //有效订单数
        List<Integer> validOrderCountList = new ArrayList<>();

        //遍历datelist集合，查询每天的有效订单书和订单总数、
        for(LocalDate date : dateList){
            //查询每天的订单总数 select count(id) from order where order_time > ? and order_time < ?
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Integer orderCount = getOrderCount(beginTime, endTime, null);


            //查询每天的有效订单数select count(id) from order where order_time >? and order_time < ? and status = 5
            Integer validOrderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);

            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
        }

        //计算时间区间内的订单总数量
        Integer totalOrderCount = orderCountList.stream().reduce(0, Integer::sum);
        //计算时间区间内的有效订单数量
        Integer validOrderCount = validOrderCountList.stream().reduce(0, Integer::sum);
        //计算订单完成率
        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0) {
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }

        return OrderReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }



    /**
     * 查询指定时间区间内订单数量
     * @param begin
     * @param end
     * @param status
     * @return
     */
    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status){
        Map map = new HashMap();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", status);
        return orderMapper.countByMap(map);
    }


    /**
     * 销量排名top10
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {

        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> salesTop10 = orderMapper.getSalesTop(beginTime, endTime);
        return SalesTop10ReportVO
                .builder()
                .nameList(StringUtils.join(salesTop10.stream().map(GoodsSalesDTO::getName).toArray(), ","))
                .numberList(StringUtils.join(salesTop10.stream().map(GoodsSalesDTO::getNumber).toArray(), ","))
                .build();
    }


}
