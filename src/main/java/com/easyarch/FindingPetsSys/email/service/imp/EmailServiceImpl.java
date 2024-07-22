package com.easyarch.FindingPetsSys.email.service.imp;


import com.easyarch.FindingPetsSys.email.mdoel.MailDto;
import com.easyarch.FindingPetsSys.email.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;


@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String sender;



    /**
     * 普通文本邮件发送
     *
     * @param mailDto 邮件实体
     * @throws MailException 发送异常
     */
    @Override
    public void sendSimpleEmail(MailDto mailDto) throws MailException {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(sender);
        mailMessage.setTo(mailDto.getRecipient());
        mailMessage.setSubject(mailDto.getSubject());
        mailMessage.setText(mailDto.getContent());
        mailSender.send(mailMessage);
        log.info("Send  SimpleEmail to '{}' success ", mailDto.getRecipient());
    }

    /**
     * html格式邮件发送
     *
     * @param mailDto 邮件实体
     * @throws MessagingException 发送异常
     */
    @Override
    public void sendHtmlEmail(MailDto mailDto) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper;
        messageHelper = new MimeMessageHelper(message, true);
        messageHelper.setFrom(sender);
        messageHelper.setTo(mailDto.getRecipient());
        message.setSubject(mailDto.getSubject());
        messageHelper.setText(mailDto.getContent(), true);
        mailSender.send(message);
        log.info("Send htmlEmail to '{}' success ", mailDto.getRecipient());
    }

    /**
     * 附件邮件发送
     *
     * @param mailDto 邮件实体
     * @throws MessagingException 发送异常
     */
    @Override
    public void sendAttachmentsEmail(MailDto mailDto) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper;
        messageHelper = new MimeMessageHelper(message, true);
        messageHelper.setFrom(sender);
        messageHelper.setTo(mailDto.getRecipient());
        messageHelper.setSubject(mailDto.getSubject());
        messageHelper.setText(mailDto.getContent(), true);
        //携带附件
        for (String resourcePath : mailDto.getResourcePaths()) {
            FileSystemResource file = new FileSystemResource(resourcePath);
            String fileName = resourcePath.substring(resourcePath.lastIndexOf(File.separator));
            messageHelper.addAttachment(fileName, file);

        }
        mailSender.send(message);
        log.info("Send AttachmentsEmail to '{}' success ", mailDto.getRecipient());
    }
}
