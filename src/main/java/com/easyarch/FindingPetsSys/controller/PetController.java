//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.controller;

import cn.hutool.core.util.StrUtil;
import com.easyarch.FindingPetsSys.dto.PetDetailDto;
import com.easyarch.FindingPetsSys.dto.Result;
import com.easyarch.FindingPetsSys.exception.NotFoundException;
import com.easyarch.FindingPetsSys.exception.OperationFailedException;
import com.easyarch.FindingPetsSys.exception.ValidatorException;
import com.easyarch.FindingPetsSys.service.PetService;
import com.easyarch.FindingPetsSys.util.UserContext;
import com.github.pagehelper.PageInfo;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping({"/pets"})
@Validated
public class PetController {
    @Autowired
    private PetService petService;

    public PetController() {
    }

    @PostMapping({""})
    public Result<String> addPet(@RequestParam("petName") String petName,
                                 @RequestParam("info") String info,
                                 @RequestParam("deviceCode") String code,
                                 @RequestParam("file") MultipartFile file) throws ValidatorException, OperationFailedException, NotFoundException, MqttException {
        return Result.created("创建宠物成功", petService.insertPet(UserContext.getUser().getUserId(), petName, info, code, file));
    }

    @DeleteMapping({"/{petId}/device"})
    public Result<String> unbindDevice(@PathVariable Long petId) throws ValidatorException, OperationFailedException, NotFoundException, MqttException {
        return Result.operate(this.petService.removeDeviceIdByPetId(UserContext.getUser().getUserId(), petId));
    }

    @PutMapping({"/{petId}/device"})
    public Result<String> bindDevice(@PathVariable Long petId, @RequestParam("deviceCode") String code) throws ValidatorException, OperationFailedException, NotFoundException, MqttException {
        return Result.operate(petService.addDeviceIdByPetId(UserContext.getUser().getUserId(), petId, code));
    }

    @DeleteMapping({"/{petId}"})
    public Result<String> deletePet(@PathVariable Long petId) throws OperationFailedException, NotFoundException, MqttException {
        return Result.operate(petService.deletePet(UserContext.getUser().getUserId(), petId));
    }

    @GetMapping({""})
    public Result<PageInfo<PetDetailDto>> pageSearchPetName(@RequestParam(value = "keyword", required = false) String petName,
                                                            @RequestParam("offset")
                                                            @Range(min = 1, message = "Number must be greater than {min}") int offset,
                                                            @RequestParam("limit")
                                                            @Range(min = 1, max = 15, message = "Number must be between {min} and {max}") int limit) throws ValidatorException {
        return Result.success(
                StrUtil.hasEmpty(petName) ?
                        petService.pageQueryPetsByUserId(UserContext.getUser().getUserId(), offset, limit) :
                        petService.pageQueryPetsByPetName(UserContext.getUser().getUserId(), petName, offset, limit));
    }
}
