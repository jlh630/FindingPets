package com.easyarch.FindingPetsSys.email.strategy;

import com.easyarch.FindingPetsSys.email.mdoel.MailDto;
import org.springframework.stereotype.Component;

@Component
public class NotifyOrderStrategy implements HandleMailStrategy{
    @Override
    public void execute(MailDto mail) {

    }
}
