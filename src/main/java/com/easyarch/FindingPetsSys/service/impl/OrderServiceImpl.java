//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alipay.api.AlipayApiException;
import com.easyarch.FindingPetsSys.aliPay.AliPayService;
import com.easyarch.FindingPetsSys.dto.OrderDetailDto;
import com.easyarch.FindingPetsSys.entity.Order;
import com.easyarch.FindingPetsSys.exception.NotFoundException;
import com.easyarch.FindingPetsSys.exception.OperationFailedException;
import com.easyarch.FindingPetsSys.exception.ServiceException;
import com.easyarch.FindingPetsSys.mapper.OrderMapper;
import com.easyarch.FindingPetsSys.rocketmq.producer.RocketMQOrderProducerService;
import com.easyarch.FindingPetsSys.service.OrderService;
import com.easyarch.FindingPetsSys.util.UserContext;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private AliPayService aliPayService;
    @Autowired
    private RocketMQOrderProducerService rocketMQOrderProducerService;

    public OrderServiceImpl() {
    }

    public String createOrder(Order order) {
        this.orderMapper.insertOrder(order);
        this.rocketMQOrderProducerService.sendDelayedOrderPay(order.getOrderId());
        return "创建成功";
    }

    /**
     * 当前帖子的订单状态是不是
     *
     * @param noteId
     * @return
     */
    public boolean isOrderPayDeposit(Long noteId) {
        byte status = orderMapper.queryOrderByNoteId(noteId).getStatus();
        return status != 0;
    }

    /**
     * 当前帖子的订单状态是不是正在进行中
     *
     * @param noteId 帖子id
     * @return Y/N
     */
    public boolean isOrderHandle(Long noteId) {
        byte status = orderMapper.queryOrderByNoteId(noteId).getStatus();
        return status == 3;
    }

    /**
     * 用户使用订单号进行支付
     *
     * @param orderId 订单id
     * @return alipay的支付页面
     * @throws OperationFailedException 冲突异常
     * @throws AlipayApiException       alipay接口异常
     * @throws NotFoundException        未找到异常
     * @throws ServiceException         alipay接口异常
     */
    public String payment(Long orderId, Long userId) throws OperationFailedException, AlipayApiException, NotFoundException, ServiceException {
        Order order = orderMapper.queryOrderByOrderId(orderId);

        if (order==null||!userId.equals(order.getUserId())) {
            throw new NotFoundException("错误的订单号");
        }
        byte status = order.getStatus();
        if (status != 0 && status != 1) {
            throw new OperationFailedException("支付无效,订单无需支付");
        }

        String res;
        if (status == 0) {
            res = aliPayService.pay(orderId.toString(), order.getDeposit(), "寻找宠物的订金");
        } else {
            res = aliPayService.pay(orderId + "-" + 1, order.getFinalPayment(), "寻找宠物的尾款");
        }
        if (StrUtil.hasEmpty(res)) {
            throw new ServiceException("调用支付接口失败");
        }
        return res;
    }


    /**
     * 数组下标转字符串状态
     *
     * @param orderStatus 下标
     * @return 状态
     */
    public String statusToString(byte orderStatus) {
        return STATUS_STRINGS[orderStatus];
    }

    /**
     * 分页我的订单
     *
     * @param userId   userid
     * @param pageNum  当前页号
     * @param pageSize 每页的数据量
     * @return 订单集合
     */

    public PageInfo<OrderDetailDto> pageSelectOrders(Long userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orders = orderMapper.queryOrdersByUserId(userId);
        PageInfo pageInfo = new PageInfo(orders);
        List<OrderDetailDto> orderDetailDtoList = orders.stream()
                .map((obj) ->
                        new OrderDetailDto(obj.getOrderId(),
                                statusToString(obj.getStatus()),
                                obj.getNoteId(), obj.getDeposit(),
                                obj.getFinalPayment(),
                                obj.getTimestamp())
                ).collect(Collectors.toList());
        pageInfo.setList(orderDetailDtoList);
        return pageInfo;
    }
}
