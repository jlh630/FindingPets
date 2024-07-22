package com.easyarch.FindingPetsSys.dto;

import com.easyarch.FindingPetsSys.entity.Permission;
import com.easyarch.FindingPetsSys.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailDto {
    private Long userId;
    private String email;
    private String userName;
    private String userImg;
    private String jti;
    private List<Role> roleList;
    private List<Permission> permissionsList;
}
