package com.easyarch.FindingPetsSys.email.service;


import com.easyarch.FindingPetsSys.email.mdoel.MailDto;


import javax.mail.MessagingException;


public interface EmailService {
    void sendSimpleEmail(MailDto mailDto);

    void sendHtmlEmail(MailDto mailDto) throws MessagingException, MessagingException;

    void sendAttachmentsEmail(MailDto mailDto) throws MessagingException;
}
