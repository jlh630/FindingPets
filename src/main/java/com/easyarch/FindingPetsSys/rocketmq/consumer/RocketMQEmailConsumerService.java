package com.easyarch.FindingPetsSys.rocketmq.consumer;

import com.alibaba.fastjson.JSON;
import com.easyarch.FindingPetsSys.email.mdoel.MailDto;
import com.easyarch.FindingPetsSys.email.service.EmailService;
import com.easyarch.FindingPetsSys.email.strategy.handleMailFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;

@Slf4j
@Service
@RocketMQMessageListener(
        topic = "email",
        consumerGroup = "emailGroup"
)
public class RocketMQEmailConsumerService implements RocketMQListener<MessageExt> {
    @Autowired
    private EmailService emailService;

    @Autowired
    private handleMailFactory handleMailFactory;

    @Override
    public void onMessage(MessageExt messageExt) {
        log.info("topic:email|consumer: {}", messageExt.getKeys());
        MailDto mailDto = JSON.parseObject(messageExt.getBody(), MailDto.class);
        //选择合适处理方式
        handleMailFactory.getStrategy(mailDto.getHandeType()).execute(mailDto);
        try {
            emailService.sendHtmlEmail(mailDto);
        } catch (MessagingException e) {
            log.warn("Send Email to '{}' failed ", mailDto.getRecipient());
        }
    }
}
