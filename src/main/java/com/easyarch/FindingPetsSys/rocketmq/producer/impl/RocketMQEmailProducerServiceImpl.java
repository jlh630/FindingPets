package com.easyarch.FindingPetsSys.rocketmq.producer.impl;

import com.easyarch.FindingPetsSys.email.mdoel.MailDto;
import com.easyarch.FindingPetsSys.rocketmq.producer.RocketMQEmailProducerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class RocketMQEmailProducerServiceImpl implements RocketMQEmailProducerService {
    @Autowired
    RocketMQTemplate rocketMQTemplate;

    public SendResult sendHandEmailMessage(MailDto mailDto) {
        String messageId = String.valueOf(UUID.randomUUID());
        SendResult result = rocketMQTemplate.syncSend("email",
                MessageBuilder
                        .withPayload(mailDto)
                        .setHeader("KEYS", messageId)
                        .build());
        log.info("produce|topic:email message:{}  success!", messageId);
        return result;
    }
}
