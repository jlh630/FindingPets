//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.service;

import com.easyarch.FindingPetsSys.dto.NoteFollowDetailDto;
import com.easyarch.FindingPetsSys.exception.NotFoundException;
import com.easyarch.FindingPetsSys.exception.OperationFailedException;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface NoteFollowService {
    String[] STATUS_STRINGS = new String[]{"进行中", "待收尾款", "完成", "未完成"};

    Long follow(Long noteId, Long userId) throws OperationFailedException, NotFoundException;

    String noFollow(Long noteId, Long userId) throws NotFoundException;


    String finish(Long noteId, Long userId, String deviceCode) throws OperationFailedException, NotFoundException;

    String statusToString(byte status);

    PageInfo<NoteFollowDetailDto> pageSelectNoteFollows(Long userId, int pageNum, int pageSize);
}
