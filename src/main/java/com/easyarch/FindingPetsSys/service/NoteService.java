//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.service;

import com.easyarch.FindingPetsSys.dto.NoteDetailDto;
import com.easyarch.FindingPetsSys.dto.NoteSummaryDto;
import com.easyarch.FindingPetsSys.entity.Note;
import com.easyarch.FindingPetsSys.exception.NotFoundException;
import com.easyarch.FindingPetsSys.exception.ValidatorException;
import com.github.pagehelper.PageInfo;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface NoteService {
    Long publishNote(Long userId, Long petId, String title, String content, boolean isPublic, BigDecimal reward, List<Long> userIds, MultipartFile[] files) throws ValidatorException, NotFoundException;

    NoteDetailDto queryNoteInfoByNoteId(Long userId,Long noteId) throws NotFoundException;

    List<String> noteResourcePathToList(String resourcePath);

    PageInfo<NoteSummaryDto> pageSelectVisibilityNotes(Long userId, int pageNum, int pageSize);

    PageInfo<NoteSummaryDto> pageSelectInviteNotes(Long userId, int pageNum, int pageSize);

    PageInfo<NoteSummaryDto> pageSelectPublicNotesByTitle(Long userId, String title, int pageNum, int pageSize);

    List<NoteSummaryDto> noteListToNoteSummaryList(List<Note> notes, Long userId);

    PageInfo<NoteSummaryDto> pageSelectNotesByUserId(Long userId, int pageNum, int pageSize);
}
