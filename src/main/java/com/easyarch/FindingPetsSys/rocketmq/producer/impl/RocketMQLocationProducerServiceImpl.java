

package com.easyarch.FindingPetsSys.rocketmq.producer.impl;

import com.easyarch.FindingPetsSys.mqtt.model.LocationMessage;
import com.easyarch.FindingPetsSys.rocketmq.producer.RocketMQLocationProducerService;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RocketMQLocationProducerServiceImpl implements RocketMQLocationProducerService {
     @Autowired
    RocketMQTemplate rocketMQTemplate;

    public RocketMQLocationProducerServiceImpl() {
    }

    public SendResult sendHandleLocationMessage(LocationMessage locationMessage, Long deviceId) {
        String messageId = String.valueOf(UUID.randomUUID());
        SendResult result = rocketMQTemplate.syncSend("location",
                MessageBuilder
                        .withPayload(locationMessage)
                        .setHeader("KEYS", messageId)
                        .setHeader("deviceId", deviceId)
                        .build());
        log.info("produce|topic:location message:{}  success!", messageId);
        return result;
    }
}
