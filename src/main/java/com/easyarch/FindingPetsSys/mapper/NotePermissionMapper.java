//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.mapper;

import com.easyarch.FindingPetsSys.entity.NotePermission;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface NotePermissionMapper {
    int insertNotePermission(@Param("notePermission") NotePermission notePermission);

    List<NotePermission> queryNotePermissionByUserId(@Param("userId") Long userId);

    List<NotePermission> queryNotePermissionByNoteId(@Param("noteId") Long noteId);

    int batchInsertNotePermission(@Param("noteId") Long noteId, @Param("userIds") List<Long> userIds);

    int deleteNotePermissionByNoteId(@Param("noteId") Long noteId);

    int deleteNotePermissionExceptUserId(@Param("noteId") Long noteId, @Param("userId") Long userId);
}
