//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.rocketmq.producer;

import com.easyarch.FindingPetsSys.mqtt.model.LocationMessage;
import org.apache.rocketmq.client.producer.SendResult;

public interface RocketMQLocationProducerService {
    SendResult sendHandleLocationMessage(LocationMessage locationMessage, Long deviceId);
}
