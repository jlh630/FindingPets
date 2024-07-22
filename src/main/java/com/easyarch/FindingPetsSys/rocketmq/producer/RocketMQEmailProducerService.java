package com.easyarch.FindingPetsSys.rocketmq.producer;


import com.easyarch.FindingPetsSys.email.mdoel.MailDto;
import org.apache.rocketmq.client.producer.SendResult;

public interface RocketMQEmailProducerService {
    SendResult sendHandEmailMessage(MailDto mailDto);
}
