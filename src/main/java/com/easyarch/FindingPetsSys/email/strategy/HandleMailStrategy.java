package com.easyarch.FindingPetsSys.email.strategy;

import com.easyarch.FindingPetsSys.email.mdoel.MailDto;

public interface HandleMailStrategy {
    void execute(MailDto mail);
}
