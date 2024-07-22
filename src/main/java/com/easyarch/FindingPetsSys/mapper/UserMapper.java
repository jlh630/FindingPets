//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.mapper;

import com.easyarch.FindingPetsSys.entity.User;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User queryUserByUserId(@Param("id") Long id);

    User queryUserByUserEmail(@Param("email") String email);

    int insertUser(@Param("user") User user);

    int updateUser(@Param("user") User user);

    List<User> listUser();

    List<User> selectUsersByUserIds(@Param("ids") List<Long> ids);
}
