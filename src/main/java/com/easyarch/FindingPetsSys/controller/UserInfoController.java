//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.controller;

import com.easyarch.FindingPetsSys.dto.HandleUserDto;
import com.easyarch.FindingPetsSys.dto.Result;
import com.easyarch.FindingPetsSys.dto.UserDetailDto;
import com.easyarch.FindingPetsSys.entity.User;
import com.easyarch.FindingPetsSys.exception.OperationFailedException;
import com.easyarch.FindingPetsSys.exception.ValidatorException;
import com.easyarch.FindingPetsSys.service.UserService;
import com.easyarch.FindingPetsSys.util.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping({"/user"})
public class UserInfoController {
    @Autowired
    private UserService userService;

    public UserInfoController() {
    }

    @GetMapping({"/profile"})
    public Result<UserDetailDto> userInfo() {
        return Result.success(UserContext.getUser());
    }

    //    @PutMapping({"/profile"})
//    public Result<String> userUpdatePassword(@RequestBody User user) throws ValidatorException, OperationFailedException {
//        return Result.operate(userService.updateUserByUser(user));
//    }
    @PutMapping({"/profile/username"})
    public Result<String> updateUserName(@RequestBody HandleUserDto user) throws ValidatorException, OperationFailedException {
        return Result.operate(userService.updateUserName(UserContext.getUser().getUserId(), user.getUsername()));
    }

    @PutMapping({"/profile/passwd"})
    public Result<String> updatePasswd(@RequestBody HandleUserDto user) throws ValidatorException, OperationFailedException {
        return Result.operate(userService.updateUserPasswd(UserContext.getUser().getUserId(),UserContext.getUser().getEmail(), user.getPassword(),user.getCode()));
    }


    @PostMapping({"/upload"})
    public Result<String> uploadImg(@RequestParam("file") MultipartFile file) throws ValidatorException {
        return Result.operate(userService.uploadImg(UserContext.getUser().getUserId(), file));
    }

    @PostMapping({"/roles/pet-detective"})
    public Result<String> upgradePetDetective() throws OperationFailedException {
        return Result.operate(userService.upgradePetDetective(UserContext.getUser().getUserId()));
    }
}
