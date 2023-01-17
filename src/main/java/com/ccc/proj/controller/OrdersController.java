package com.ccc.proj.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ccc.proj.common.BaseContext;
import com.ccc.proj.common.R;
import com.ccc.proj.dto.OrdersDto;
import com.ccc.proj.entity.OrderDetail;
import com.ccc.proj.entity.Orders;
import com.ccc.proj.service.OrderDetailService;
import com.ccc.proj.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("orders:{}",orders);
        ordersService.submit(orders);
        return R.success("下单成功");
    }

    /**
     * 移动端分页查询历史订单
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> userPge(int page,int pageSize){
        log.info("page={},pageSize={}",page,pageSize);
        //构造分页构造器对象
        Page<Orders> ordersPage = new Page<>(page,pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>();
        //添加查询条件
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        queryWrapper.orderByDesc(Orders::getCheckoutTime);
        ordersService.page(ordersPage, queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(ordersPage,ordersDtoPage,"records");

        List<Orders> records = ordersPage.getRecords();

        List<OrdersDto> ordersDtoList = records.stream().map((item) -> {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item,ordersDto);

            Long orderId = item.getId();
            LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OrderDetail::getOrderId,orderId);
            List<OrderDetail> list = orderDetailService.list(wrapper);
            ordersDto.setOrderDetails(list);
            return ordersDto;
        }).collect(Collectors.toList());

        ordersDtoPage.setRecords(ordersDtoList);

        return R.success(ordersDtoPage);
    }

    /**
     * 后台订单明细分页查询
     * @param page
     * @param pageSize
     * @param number
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,Long number,String beginTime,String endTime){
        log.info("page={},pageSize={},number={},begin:{},endTime:{}",page,pageSize,number,beginTime,endTime);

        //构造分页构造器对象
        Page<Orders> ordersPage = new Page<>(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(number != null,Orders::getNumber,number);
        queryWrapper.between(beginTime != null && endTime != null,Orders::getCheckoutTime,beginTime,endTime);
        queryWrapper.orderByDesc(Orders::getCheckoutTime);

        ordersService.page(ordersPage,queryWrapper);

        return R.success(ordersPage);
    }

    /**
     * 更新订单信息
     * @param orders
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Orders orders){
        log.info("orders:{}",orders);
        Long orderId = orders.getId();
        Orders order = ordersService.getById(orderId);
        order.setStatus(orders.getStatus());
        ordersService.updateById(order);
        return R.success("更新成功");
    }
}
