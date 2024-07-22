//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.service;

import com.easyarch.FindingPetsSys.exception.AuthenticationException;
import com.easyarch.FindingPetsSys.exception.OperationFailedException;
import com.easyarch.FindingPetsSys.exception.ValidatorException;

public interface LoginService {
    String login(String email, String password) throws ValidatorException, AuthenticationException;

    String loginEmail(String email, String code) throws ValidatorException, AuthenticationException;

    String register(String email,String passwd,String code) throws ValidatorException, OperationFailedException;

    String logout();




}
