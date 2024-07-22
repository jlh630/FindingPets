//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.service;

import com.easyarch.FindingPetsSys.dto.PetDetailDto;
import com.easyarch.FindingPetsSys.exception.NotFoundException;
import com.easyarch.FindingPetsSys.exception.OperationFailedException;
import com.easyarch.FindingPetsSys.exception.ValidatorException;
import com.github.pagehelper.PageInfo;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface PetService {
    String insertPet(Long userId, String petName, String info, String code, MultipartFile file) throws ValidatorException, OperationFailedException, NotFoundException;

    String deletePet(Long userId, Long petId) throws OperationFailedException, NotFoundException;

    String removeDeviceIdByPetId(Long userId, Long petId) throws ValidatorException, OperationFailedException, NotFoundException;

    String addDeviceIdByPetId(Long userId, Long petId, String code) throws ValidatorException, OperationFailedException, NotFoundException;

    PetDetailDto queryPetInfoByPetId(Long petId) throws NotFoundException;

    PageInfo<PetDetailDto> pageQueryPetsByUserId(Long userId, int pageNum, int pageSize);

    PageInfo<PetDetailDto> pageQueryPetsByPetName(Long userId, String petName, int pageNum, int pageSize) throws ValidatorException;
}
