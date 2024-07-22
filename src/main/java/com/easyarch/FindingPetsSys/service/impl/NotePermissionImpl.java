//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.service.impl;

import com.easyarch.FindingPetsSys.mapper.NotePermissionMapper;
import com.easyarch.FindingPetsSys.service.NotePermissionService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotePermissionImpl implements NotePermissionService {
    @Autowired
    private NotePermissionMapper notePermissionMapper;

    public NotePermissionImpl() {
    }

    /**
     * 批量插入访问帖子的权限
     *
     * @param noteId  帖子id
     * @param userIds 能访问该帖子权限的用户id集合
     * @return 信息
     */
    public String bathInsertNotePermission(Long noteId, List<Long> userIds) {
        notePermissionMapper.batchInsertNotePermission(noteId, userIds);
        return "批量插入成功";
    }
}
