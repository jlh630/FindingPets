//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.rocketmq.consumer;

import com.alibaba.fastjson.JSON;
import com.easyarch.FindingPetsSys.entity.Order;
import com.easyarch.FindingPetsSys.mapper.NoteMapper;
import com.easyarch.FindingPetsSys.mapper.NotePermissionMapper;
import com.easyarch.FindingPetsSys.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RocketMQMessageListener(
        consumerGroup = "orderPayGroup",
        topic = "order",
        selectorExpression = "pay"
)
public class RocketMQOrderPayConsumerService implements RocketMQListener<MessageExt> {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private NotePermissionMapper notePermissionMapper;
    @Autowired
    private NoteMapper noteMapper;

    public RocketMQOrderPayConsumerService() {
    }

    @Transactional
    public void onMessage(MessageExt messageExt) {
        log.info("topic:order(pay)|consumer: {}", messageExt.getKeys());
        Long orderId = JSON.parseObject(messageExt.getBody(), Long.class);
        Order order = orderMapper.queryOrderByOrderId(orderId);
        /*订单未支付
        1.设置订单状态
        2.删除帖子的权限
        3.修改帖子为私密
        */
        if (order != null && order.getStatus() == 0) {
            order.setStatus((byte) 4);
            orderMapper.updateOrderStatus(order);
            notePermissionMapper.deleteNotePermissionByNoteId(order.getNoteId());
            noteMapper.updateNoteVisibility(order.getNoteId(), false);
            log.info("topic:order(pay)|consumer: {} success order update", messageExt.getKeys());
        }
    }
}
