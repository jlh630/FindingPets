package com.easyarch.FindingPetsSys.service;

import com.easyarch.FindingPetsSys.exception.OperationFailedException;
import com.easyarch.FindingPetsSys.exception.ValidatorException;



public interface EmailCodeService {
    String loginCaptcha(String email) throws ValidatorException, OperationFailedException;

    String registerCaptcha(String email) throws ValidatorException, OperationFailedException;

    String editUserPasswdCaptcha(String email) ;
}
