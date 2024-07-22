//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.rocketmq.consumer;

import com.alibaba.fastjson.JSON;
import com.easyarch.FindingPetsSys.aliPay.AliPayService;
import com.easyarch.FindingPetsSys.entity.Note;
import com.easyarch.FindingPetsSys.entity.Order;
import com.easyarch.FindingPetsSys.mapper.*;

import java.math.BigDecimal;

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
        topic = "order",
        consumerGroup = "orderFinishGroup",
        selectorExpression = "finish"
)
public class RocketMQOrderFinishConsumerService implements RocketMQListener<MessageExt> {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private NoteMapper noteMapper;
    @Autowired
    private AliPayService aliPayService;
    @Autowired
    private NotePermissionMapper notePermissionMapper;
    @Autowired
    private LocationPermissionMapper locationPermissionMapper;
    @Autowired
    private NoteFollowMapper noteFollowMapper;

    public RocketMQOrderFinishConsumerService() {
    }

    @Transactional
    public void onMessage(MessageExt messageExt) {
        log.info("topic:order(finish)|consumer: {}", messageExt.getKeys());
        Long orderId = JSON.parseObject(messageExt.getBody(), Long.class);
        Order order = orderMapper.queryOrderByOrderId(orderId);
        /*订单没有人完成
        1.改变订单状态
        2.帖子改成私密/删除帖子的访问权限
        3.支付宝退回定金
        4.修改关注列表状态
        5.删除访问位置的权限
        */
        if (order != null && order.getStatus() == 3) {
            Note note = noteMapper.queryNoteByNoteId(order.getNoteId());
            order.setStatus((byte) 5);
            orderMapper.updateOrderStatus(order);
            if (!note.isPublicly()) {
                notePermissionMapper.deleteNotePermissionByNoteId(note.getNoteId());
            }
            noteMapper.updateNoteVisibility(note.getNoteId(), false);
            aliPayService.refund(orderId.toString(), order.getDeposit().multiply(new BigDecimal("0.35")), "超时未有人完成");
            noteFollowMapper.updateAllNoteFollowStatusByNoteId(note.getNoteId(), (byte) 3);
            locationPermissionMapper.deleteAllLocationPermission(note.getPetId());
            log.info("topic:order(finish)|consumer: {} success order update", messageExt.getKeys());
        }

    }
}
