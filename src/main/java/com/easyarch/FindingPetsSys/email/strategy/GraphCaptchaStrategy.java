package com.easyarch.FindingPetsSys.email.strategy;

import com.easyarch.FindingPetsSys.email.mdoel.MailDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class GraphCaptchaStrategy implements HandleMailStrategy {

    @Autowired
    private TemplateEngine templateEngine;

    @Override
    public void execute(MailDto mail) {
        Context context = new Context();
        context.setVariable("base64Image", mail.getContent());

        String htmlContent = templateEngine.process("Captcha-email", context);
        mail.setContent(htmlContent);
    }
}
