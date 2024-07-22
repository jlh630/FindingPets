//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.mapper;

import com.easyarch.FindingPetsSys.entity.NoteFollow;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface NoteFollowMapper {
    int insertNoteFollow(@Param("noteFollow") NoteFollow noteFollow);

    int updateNoteFollow(@Param("noteFollow") NoteFollow noteFollow);

    int deleteNoteFollow(@Param("noteFollow") NoteFollow noteFollow);

    NoteFollow queryNoteFollow(@Param("userId") Long userId, @Param("noteId") Long noteId);

    List<NoteFollow> selectNoteFollowsByUserId(@Param("userId") Long userId);

    int updateNoteFollowStatusExceptUserId(@Param("noteId") Long noteId, @Param("userId") Long userId, @Param("status") byte status);

    int updateAllNoteFollowStatusByNoteId(@Param("noteId") Long noteId, @Param("status") byte status);

    int updateNoteFollowStatusByNoteIdAndStatus(@Param("noteId") Long noteId, @Param("status") byte status, @Param("updateStatus") byte updateStatus);

}
