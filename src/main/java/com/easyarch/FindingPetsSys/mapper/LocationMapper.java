//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.mapper;

import com.easyarch.FindingPetsSys.entity.Location;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LocationMapper {
    int insertLocation(@Param("location") Location var1);

    List<Location> selectTodayLocationsByPetId(@Param("petId") Long petId);

    Location queryMaxLocationByPetId(@Param("petId") Long petId);
}
