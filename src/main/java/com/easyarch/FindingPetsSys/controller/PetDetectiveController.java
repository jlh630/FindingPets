//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.controller;

import com.easyarch.FindingPetsSys.dto.Result;
import com.easyarch.FindingPetsSys.dto.UserSummaryDto;
import com.easyarch.FindingPetsSys.service.PetDetectiveService;

import java.util.List;

import com.easyarch.FindingPetsSys.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/petDetectives"})
public class PetDetectiveController {
    @Autowired
    private PetDetectiveService petDetectiveService;

    public PetDetectiveController() {
    }

    @GetMapping({""})
    public Result<List<UserSummaryDto>> petDetectiveList() {
        return Result.success(petDetectiveService.selectPetDetectiveId(UserContext.getUser().getUserId()));
    }
}
