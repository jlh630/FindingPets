package com.easyarch.FindingPetsSys.mapper;

import com.easyarch.FindingPetsSys.entity.Note;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author jlh
* @description 针对表【notes】的数据库操作Mapper
* @createDate 2024-06-30 04:22:04
* @Entity com/easyarch/FindingPetsSys.domain.Notes
*/
@Mapper
public interface NoteMapper {
    int insertNote(@Param("note") Note note);

    Note queryNoteByNoteId(@Param("id") Long id);

    List<Note> queryNotesByPetId(@Param("petId") Long petId);

    int updateNote(@Param("note") Note note);

    int updateNoteVisibility(@Param("id") Long id, @Param("visibility") boolean visibility);

    List<Note> selectNotesByPublicAndVisibility(@Param("isPublic") boolean isPublic, @Param("visibility") boolean visibility);

    List<Note> selectNotesByPublicAndVisibilityAndTitle(@Param("isPublic") boolean isPublic, @Param("visibility") boolean visibility, @Param("title") String title);

    List<Note> selectNotesByNoteIds(@Param("ids") List<Long> ids);

    List<Note> selectNotesByUserId(@Param("userId") Long userId);


}
