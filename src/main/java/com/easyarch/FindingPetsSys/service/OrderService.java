//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.service;

import com.alipay.api.AlipayApiException;
import com.easyarch.FindingPetsSys.dto.OrderDetailDto;
import com.easyarch.FindingPetsSys.entity.Order;
import com.easyarch.FindingPetsSys.exception.NotFoundException;
import com.easyarch.FindingPetsSys.exception.OperationFailedException;
import com.easyarch.FindingPetsSys.exception.ServiceException;
import com.github.pagehelper.PageInfo;
import java.util.List;

public interface OrderService {
    String[] STATUS_STRINGS = new String[]{"待支付订金", "待支付尾款", "订单完成", "订单进行中", "订单过期", "退回订金"};

    String createOrder(Order order);

    boolean isOrderPayDeposit(Long noteId);

    boolean isOrderHandle(Long noteId);

    String payment(Long orderId,Long userId) throws OperationFailedException, AlipayApiException, NotFoundException, ServiceException;


    String statusToString(byte orderStatus);

    PageInfo<OrderDetailDto> pageSelectOrders(Long userId, int pageNum, int pageSize);
}
