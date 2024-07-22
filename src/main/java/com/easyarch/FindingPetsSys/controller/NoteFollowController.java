//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.controller;

import com.easyarch.FindingPetsSys.dto.NoteFollowDetailDto;
import com.easyarch.FindingPetsSys.dto.Result;
import com.easyarch.FindingPetsSys.exception.NotFoundException;
import com.easyarch.FindingPetsSys.exception.OperationFailedException;
import com.easyarch.FindingPetsSys.service.NoteFollowService;
import com.easyarch.FindingPetsSys.util.UserContext;
import com.github.pagehelper.PageInfo;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/noteFollows"})
public class NoteFollowController {
    @Autowired
    private NoteFollowService noteFollowService;

    public NoteFollowController() {
    }

    @PostMapping({"/{noteId}"})
    public Result<Long> follow(@PathVariable Long noteId) throws OperationFailedException, NotFoundException {
        return Result.created("关注成功", noteFollowService.follow(noteId, UserContext.getUser().getUserId()));
    }

    @DeleteMapping({"/{noteId}"})
    public Result<String> noFollow(@PathVariable Long noteId) throws NotFoundException {
        return Result.operate(noteFollowService.noFollow(noteId, UserContext.getUser().getUserId()));
    }

    @PostMapping({"/{noteId}/complete"})
    public Result<String> finish(@PathVariable Long noteId, @RequestParam("deviceCode") String deviceCode) throws OperationFailedException, NotFoundException {
        return Result.operate(noteFollowService.finish(noteId, UserContext.getUser().getUserId(), deviceCode));
    }

    @GetMapping({""})
    public Result<PageInfo<NoteFollowDetailDto>> pageAllFollow(@RequestParam("offset")
                                                               @Range(min = 1, message = "Number must be greater than {min}") int offset,
                                                               @RequestParam("limit")
                                                               @Range(min = 1, max = 15, message = "Number must be between {min} and {max}") int limit) {
        return Result.success(noteFollowService.pageSelectNoteFollows(UserContext.getUser().getUserId(), offset, limit));
    }
}
