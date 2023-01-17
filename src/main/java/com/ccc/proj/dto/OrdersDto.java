package com.ccc.proj.dto;

import com.ccc.proj.entity.OrderDetail;
import com.ccc.proj.entity.Orders;
import lombok.Data;
import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
