//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.rocketmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.easyarch.FindingPetsSys.entity.Location;
import com.easyarch.FindingPetsSys.mqtt.model.LocationMessage;
import com.easyarch.FindingPetsSys.service.LocationService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RocketMQMessageListener(
        topic = "location",
        consumerGroup = "locationHandler"
)
public class RocketMQLocationConsumerService implements RocketMQListener<MessageExt>, RocketMQPushConsumerLifecycleListener {
    @Autowired
    private LocationService locationService;

    public RocketMQLocationConsumerService() {
    }

    @SneakyThrows
    @Transactional
    public void onMessage(MessageExt messageExt) {
        log.info("topic:location|consumer: {}", messageExt.getKeys());

            //位置
            LocationMessage locationMessage = JSON.parseObject(messageExt.getBody(), LocationMessage.class);
            String[] parts = locationMessage.getTime().split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            int seconds = Integer.parseInt(parts[2]);
            LocalDateTime dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(hours, minutes, seconds));
            Date date = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());

            Long deviceId = Long.parseLong(messageExt.getUserProperty("deviceId"));
            Location location = new Location(null, locationMessage.getLongitude(), locationMessage.getLatitude(), date, deviceId, null);
            //添加位置信息
            locationService.saveLocation(location);
            log.info("topic:location|consumer: {} insert location success", messageExt.getKeys());

    }

    public void prepareStart(DefaultMQPushConsumer consumer) {
        consumer.setMaxReconsumeTimes(3);
    }
}
