package com.easyarch.FindingPetsSys.controller;

import com.easyarch.FindingPetsSys.dto.HandleUserDto;
import com.easyarch.FindingPetsSys.dto.Result;
import com.easyarch.FindingPetsSys.exception.OperationFailedException;
import com.easyarch.FindingPetsSys.exception.ValidatorException;
import com.easyarch.FindingPetsSys.service.EmailCodeService;
import com.easyarch.FindingPetsSys.service.LoginService;
import com.easyarch.FindingPetsSys.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("")
public class EmailCodeController {
    @Autowired
    private EmailCodeService emailCodeService;

    @PostMapping({"/captcha/login"})
    public Result<String> loginCaptcha(@RequestBody HandleUserDto user) throws OperationFailedException, ValidatorException {
        return Result.created(emailCodeService.loginCaptcha(user.getEmail()), null);
    }

    @PostMapping({"/captcha/register"})
    public Result<String> registerCaptcha(@RequestBody HandleUserDto user) throws OperationFailedException, ValidatorException {
        return Result.created(emailCodeService.registerCaptcha(user.getEmail()), null);
    }

    @PostMapping({"/captcha/edit/passwd"})
    public Result<String> editPasswdCaptcha()    {
        return Result.created(emailCodeService.editUserPasswdCaptcha(UserContext.getUser().getEmail()), null);
    }
}
