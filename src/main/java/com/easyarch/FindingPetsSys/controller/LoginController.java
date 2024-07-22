//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.controller;

import com.easyarch.FindingPetsSys.dto.HandleUserDto;
import com.easyarch.FindingPetsSys.dto.Result;
import com.easyarch.FindingPetsSys.exception.AuthenticationException;
import com.easyarch.FindingPetsSys.exception.OperationFailedException;
import com.easyarch.FindingPetsSys.exception.ValidatorException;
import com.easyarch.FindingPetsSys.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({""})
public class LoginController {
    @Autowired
    private LoginService loginService;

    @PostMapping({"/login"})
    public Result<String> login(@RequestBody HandleUserDto user) throws ValidatorException, AuthenticationException {
        return Result.success(loginService.login(user.getEmail(), user.getPassword()));
    }

    @PostMapping("/login/verify-code")
    public Result<String> loginEmail(@RequestBody HandleUserDto user) throws ValidatorException, AuthenticationException {
        return Result.success(loginService.loginEmail(user.getEmail(), user.getCode()));
    }

    @PostMapping({"/register"})
    public Result<String> register(@RequestBody HandleUserDto user) throws ValidatorException, OperationFailedException {
        return Result.created(loginService.register(user.getEmail(), user.getPassword(), user.getCode()), null);
    }


    @PostMapping({"/logout"})
    public Result<String> logout() {
        return Result.success(loginService.logout());
    }
}
