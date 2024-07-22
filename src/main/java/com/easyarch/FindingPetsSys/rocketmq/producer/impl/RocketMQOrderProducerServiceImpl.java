//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.rocketmq.producer.impl;

import com.easyarch.FindingPetsSys.rocketmq.producer.RocketMQOrderProducerService;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RocketMQOrderProducerServiceImpl implements RocketMQOrderProducerService {
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public RocketMQOrderProducerServiceImpl() {
    }

    public SendResult sendDelayedOrderPay(Long orderId) {
        String messageId = String.valueOf(UUID.randomUUID());
        SendResult result = this.rocketMQTemplate.syncSendDelayTimeSeconds(
                "order:pay",
                MessageBuilder
                        .withPayload(orderId)
                        .setHeader("KEYS", messageId)
                        .build(),
                600L);
        log.info("produce|topic:order message:{} tag:pay success!", messageId);
        return result;
    }

    public SendResult sendDelayOrderFinish(Long orderId) {
        String messageId = String.valueOf(UUID.randomUUID());
        SendResult result = rocketMQTemplate.syncSendDelayTimeSeconds("order:finish",
                MessageBuilder
                        .withPayload(orderId)
                        .setHeader("KEYS", messageId)
                        .build(),
                86400L);
        log.info("produce|topic:order message:{} tag:finish success!", messageId);
        return result;
    }
}
