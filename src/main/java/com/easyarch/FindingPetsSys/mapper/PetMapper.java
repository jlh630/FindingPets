//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.mapper;

import com.easyarch.FindingPetsSys.entity.Pet;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PetMapper {
    int insertPet(@Param("pet") Pet pet);

    int deletePetByPetId(@Param("id") Long id);

    int updatePet(@Param("pet") Pet pet);

    int updatePetDeviceIdByPetId(@Param("deviceId") Long deviceId, @Param("petId") Long petId);

    Pet queryPetByPetId(@Param("id") Long id);

    Pet queryPetByPetIdAndUserId(@Param("userId") Long userId, @Param("petId") Long petId);

    List<Pet> selectPetsByUserId(@Param("userId") Long userId);

    List<Pet> selectPetsByPetName(@Param("userId") Long userId, @Param("petName") String petName);
}
