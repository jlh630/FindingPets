

package com.easyarch.FindingPetsSys.controller;

import cn.hutool.core.util.StrUtil;
import com.easyarch.FindingPetsSys.annotation.AuthPermission;
import com.easyarch.FindingPetsSys.dto.NoteDetailDto;
import com.easyarch.FindingPetsSys.dto.NoteSummaryDto;
import com.easyarch.FindingPetsSys.dto.Result;
import com.easyarch.FindingPetsSys.exception.NotFoundException;
import com.easyarch.FindingPetsSys.exception.ValidatorException;
import com.easyarch.FindingPetsSys.service.NoteService;
import com.easyarch.FindingPetsSys.util.UserContext;
import com.github.pagehelper.PageInfo;

import java.math.BigDecimal;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
@Slf4j
@Validated
@RestController
@RequestMapping({"/notes"})
public class NoteController {
    @Autowired
    private NoteService noteService;

    public NoteController() {
    }

    @PostMapping({""})
    public Result<Long> publish(@RequestParam("petId") Long petId,
                                @RequestParam("title") String title,
                                @RequestParam("content") String content,
                                @RequestParam("visibility") boolean visibility,
                                @RequestParam("reward") BigDecimal reward,
                                @RequestParam(value = "userIds", required = false) List<Long> userIds,
                                @RequestParam(value = "files", required = false) MultipartFile[] files) throws ValidatorException, NotFoundException {
        log.info("start img");
        return Result.created("发布成功", noteService.publishNote(UserContext.getUser().getUserId(), petId, title, content, visibility, reward, userIds, files));
    }

    @GetMapping({"/{noteId}"})
    public Result<NoteDetailDto> noteInfo(@PathVariable Long noteId) throws NotFoundException {
        return Result.success(noteService.queryNoteInfoByNoteId(UserContext.getUser().getUserId(), noteId));
    }

    @GetMapping({"/public"})
    public Result<PageInfo<NoteSummaryDto>> pagePublicNotesByTitle(@RequestParam("offset")
                                                                   @Range(min = 1, message = "Number must be greater than {min}") int offset,
                                                                   @RequestParam("limit")
                                                                   @Range(min = 1, max = 15, message = "Number must be between {min} and {max}") int limit,
                                                                   @RequestParam(value = "keyword", required = false) String title) {
        return Result.success(StrUtil.hasEmpty(title) ? noteService.pageSelectVisibilityNotes(UserContext.getUser().getUserId(), offset, limit) :
                noteService.pageSelectPublicNotesByTitle(UserContext.getUser().getUserId(), title, offset, limit));
    }

    @AuthPermission("petDetective:notes")
    @GetMapping({"/invited"})
    public Result<PageInfo<NoteSummaryDto>> pageInviteNotes(@RequestParam("offset")
                                                            @Range(min = 1, message = "Number must be greater than {min}") int offset,
                                                            @RequestParam("limit")
                                                            @Range(min = 1, max = 15, message = "Number must be between {min} and {max}") int limit) {
        return Result.success(noteService.pageSelectInviteNotes(UserContext.getUser().getUserId(), offset, limit));
    }

    @GetMapping({"/self"})
    public Result<PageInfo<NoteSummaryDto>> pageMyNotes(@RequestParam("offset")
                                                        @Range(min = 1, message = "Number must be greater than {min}") int offset,
                                                        @RequestParam("limit")
                                                        @Range(min = 1, max = 15, message = "Number must be between {min} and {max}") int limit) {
        return Result.success(noteService.pageSelectNotesByUserId(UserContext.getUser().getUserId(), offset, limit));
    }
}
