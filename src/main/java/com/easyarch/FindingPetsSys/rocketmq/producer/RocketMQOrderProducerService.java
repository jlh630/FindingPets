//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.rocketmq.producer;

import org.apache.rocketmq.client.producer.SendResult;

public interface RocketMQOrderProducerService {
    SendResult sendDelayedOrderPay(Long orderId);

    SendResult sendDelayOrderFinish(Long orderId);
}
