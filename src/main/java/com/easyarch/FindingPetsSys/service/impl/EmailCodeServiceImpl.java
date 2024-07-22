package com.easyarch.FindingPetsSys.service.impl;

import cn.hutool.core.lang.Validator;
import com.easyarch.FindingPetsSys.dto.CaptchaDto;
import com.easyarch.FindingPetsSys.email.mdoel.MailDto;
import com.easyarch.FindingPetsSys.entity.User;
import com.easyarch.FindingPetsSys.exception.OperationFailedException;
import com.easyarch.FindingPetsSys.exception.ValidatorException;
import com.easyarch.FindingPetsSys.redis.RedisEmailCodeService;
import com.easyarch.FindingPetsSys.redis.RedisUserService;
import com.easyarch.FindingPetsSys.rocketmq.producer.RocketMQEmailProducerService;
import com.easyarch.FindingPetsSys.service.EmailCodeService;
import com.easyarch.FindingPetsSys.util.CaptchaGeneratorUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class EmailCodeServiceImpl implements EmailCodeService {

    @Autowired
    private RocketMQEmailProducerService rocketMQEmailProducerService;

    @Autowired
    private RedisUserService redisUserService;

    @Autowired
    private RedisEmailCodeService redisEmailCodeService;

    /**
     * 登录 发送验证码
     *
     * @param email 邮箱
     * @return 信息
     * @throws ValidatorException       参数异常
     * @throws OperationFailedException 冲突异常
     */
    @Transactional
    @Override
    public String loginCaptcha(String email) throws ValidatorException, OperationFailedException {
        if (!Validator.isEmail(email)) {
            throw new ValidatorException("邮箱格式有误！");
        }
        User user = redisUserService.queryUserByUserEmail(email);
        if (user == null) {
            throw new OperationFailedException("该邮箱还未注册！");
        }
        String service="login";
        String code = redisEmailCodeService.queryEmailCode(service,email);
        if (code == null) {
            CaptchaDto captchaDto = CaptchaGeneratorUtil.GraphCaptchaNum(6);
            redisEmailCodeService.expireEmailCode(service,email, captchaDto.getCode());
            MailDto mailDto = new MailDto(email, "登录验证码", captchaDto.getCaptchaImgBase64(), 1, null);
            SendResult sendResult = rocketMQEmailProducerService.sendHandEmailMessage(mailDto);
            return "等待验证码...";
        } else {
            return "不必重复发送验证码...";
        }
    }

    /**
     * 注册 发送验证码
     *
     * @param email 邮箱
     * @return 信息
     * @throws ValidatorException       参数异常
     * @throws OperationFailedException 冲突异常
     */

    @Transactional
    @Override
    public String registerCaptcha(String email) throws ValidatorException, OperationFailedException {
        if (!Validator.isEmail(email)) {
            throw new ValidatorException("邮箱格式有误！");
        }
        User user = redisUserService.queryUserByUserEmail(email);
        if (user != null) {
            throw new OperationFailedException("该邮箱已经被注册了！");
        }
        String service="register";
        String code = redisEmailCodeService.queryEmailCode(service,email);
        if (code == null) {
            CaptchaDto captchaDto = CaptchaGeneratorUtil.GraphCaptchaNum(6);
            redisEmailCodeService.expireEmailCode(service,email, captchaDto.getCode());
            MailDto mailDto = new MailDto(email, "注册验证码", captchaDto.getCaptchaImgBase64(), 1, null);
            SendResult sendResult = rocketMQEmailProducerService.sendHandEmailMessage(mailDto);
            return "等待验证码...";
        } else {
            return "不必重复发送验证码...";
        }
    }

    /**
     * 修改密码 发送验证码
     *
     * @param email 邮箱
     * @return 信息
     */
    @Override
    public String editUserPasswdCaptcha(String email) {
        String service="edit_ps";
        String code = redisEmailCodeService.queryEmailCode(service,email);
        if (code == null) {
            CaptchaDto captchaDto = CaptchaGeneratorUtil.GraphCaptchaNum(6);
            redisEmailCodeService.expireEmailCode(service,email, captchaDto.getCode());
            MailDto mailDto = new MailDto(email, "用户修改密码", captchaDto.getCaptchaImgBase64(), 1, null);
            SendResult sendResult = rocketMQEmailProducerService.sendHandEmailMessage(mailDto);
            return "等待验证码...";
        } else {
            return "不必重复发送验证码...";
        }
    }
}
