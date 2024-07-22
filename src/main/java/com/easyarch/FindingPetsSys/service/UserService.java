//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.service;

import com.easyarch.FindingPetsSys.dto.UserDetailDto;
import com.easyarch.FindingPetsSys.entity.User;
import com.easyarch.FindingPetsSys.exception.OperationFailedException;
import com.easyarch.FindingPetsSys.exception.ValidatorException;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserDetailDto findUserPermissionByUserId(Long userId);

    String updateUserByUser(User upUser) throws ValidatorException, OperationFailedException;

    String updateUserName(Long userId,String name) throws ValidatorException, OperationFailedException;

    String updateUserPasswd(Long userId,String userEmail,String password,String code) throws ValidatorException, OperationFailedException;

    String upgradePetDetective(Long userId) throws OperationFailedException;

    String uploadImg(Long userId, MultipartFile file) throws ValidatorException;
}
