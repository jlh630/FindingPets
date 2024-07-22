//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.service.impl;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import com.easyarch.FindingPetsSys.dto.CaptchaDto;
import com.easyarch.FindingPetsSys.email.mdoel.MailDto;
import com.easyarch.FindingPetsSys.entity.User;
import com.easyarch.FindingPetsSys.entity.UserRole;
import com.easyarch.FindingPetsSys.exception.AuthenticationException;
import com.easyarch.FindingPetsSys.exception.OperationFailedException;
import com.easyarch.FindingPetsSys.exception.ValidatorException;
import com.easyarch.FindingPetsSys.mapper.UserMapper;
import com.easyarch.FindingPetsSys.mapper.UserRoleMapper;
import com.easyarch.FindingPetsSys.redis.RedisEmailCodeService;
import com.easyarch.FindingPetsSys.redis.RedisTokenListService;
import com.easyarch.FindingPetsSys.redis.RedisUserService;
import com.easyarch.FindingPetsSys.rocketmq.producer.RocketMQEmailProducerService;
import com.easyarch.FindingPetsSys.service.LoginService;
import com.easyarch.FindingPetsSys.util.*;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private RedisUserService redisUserService;
    @Autowired
    private RedisTokenListService redisTokenListService;
    @Autowired
    private RedisEmailCodeService redisEmailCodeService;
    @Autowired
    private RocketMQEmailProducerService RocketMQEmailProducerService;

    public LoginServiceImpl() {
    }

    /**
     * 密码的方式登录
     *
     * @param email    用户邮箱
     * @param password 用户密码
     * @return token
     * @throws ValidatorException      参数异常
     * @throws AuthenticationException
     */
    public String login(String email, String password) throws ValidatorException, AuthenticationException {
        if (!Validator.isEmail(email)) {
            throw new ValidatorException("邮箱格式有误！");
        }

        if (StrUtil.isEmpty(password) || password.length() < 6) {
            throw new ValidatorException("密码格式有误或长度不够至少6位");
        }

        String md5Passwd = Md5Util.encode(password);
        User user = redisUserService.queryUserByUserEmail(email);
        if (user != null && user.getPassword().equals(md5Passwd)) {
            Map<String, String> payload = new HashMap<>();
            payload.put("uid", String.valueOf(user.getUserId()));
            String jti = JwtUtil.createJTI();
            redisTokenListService.setTokenId(jti);
            return JwtUtil.createToken(jti, payload);
        } else {
            throw new AuthenticationException("账号密码错误");
        }
    }

    /**
     * 邮箱的方式登录
     *
     * @param email 邮箱
     * @param code  验证码
     * @return 信息
     */

    @Override
    public String loginEmail(String email, String code) throws ValidatorException, AuthenticationException {
        if (!Validator.isEmail(email)) {
            throw new ValidatorException("邮箱格式有误！");
        }
        if (StrUtil.isEmpty(code) || code.length() != 6) {
            throw new ValidatorException("验证码格式错误！");
        }
        String emailCode = redisEmailCodeService.queryEmailCode("login",email);
        if (StrUtil.isEmpty(emailCode) || !StrUtil.equals(emailCode, code)) {
            throw new AuthenticationException("验证码错误");
        }

        User user = redisUserService.queryUserByUserEmail(email);
        Map<String, String> payload = new HashMap<>();
        payload.put("uid", String.valueOf(user.getUserId()));
        String jti = JwtUtil.createJTI();
        redisTokenListService.setTokenId(jti);
        return JwtUtil.createToken(jti, payload);
    }

    /**
     * 注册
     *
     * @return 信息
     * @throws ValidatorException       参数异常
     * @throws OperationFailedException 冲突异常
     */
    @Transactional
    public String register(String email, String passwd, String code) throws ValidatorException, OperationFailedException {
        if (!Validator.isEmail(email)) {
            throw new ValidatorException("邮箱格式有误！");
        }

        if (StrUtil.isEmpty(passwd) || passwd.length() < 6) {
            throw new ValidatorException("密码长度不够至少6位");
        }
        if (StrUtil.isEmpty(code) || code.length() != 6) {
            throw new ValidatorException("验证码格式错误");
        }

        User findUser = redisUserService.queryUserByUserEmail(email);
        if (findUser != null) {
            throw new OperationFailedException("注册失败，有此用户");
        }
        String emailCode = redisEmailCodeService.queryEmailCode("register",email);
        if (StrUtil.isEmpty(emailCode) || !StrUtil.equals(emailCode, code)) {
            throw new OperationFailedException("验证码错误");
        }
        String md5Passwd = Md5Util.encode(passwd);
        User insertUser = new User();
        insertUser.setEmail(email);
        insertUser.setPassword(md5Passwd);
        insertUser.setUserName(NickNameUtil.getName());
        userMapper.insertUser(insertUser);
        userRoleMapper.insertUserRole(new UserRole(insertUser.getUserId(), 2L));
        return "注册成功";
    }

    /**
     * 退出登录
     *
     * @return 信息
     */
    public String logout() {
        redisTokenListService.removeTokenId(UserContext.getUser().getJti());
        return "退出登录成功";
    }


}
