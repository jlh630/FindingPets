package com.easyarch.FindingPetsSys.util;


import com.easyarch.FindingPetsSys.dto.UserDetailDto;

public class UserContext {
    private static final ThreadLocal<UserDetailDto> userContext = new ThreadLocal<>();


    public static void saveUser(UserDetailDto userInfo) {
        userContext.set(userInfo);
    }

    public static UserDetailDto getUser() {
        return (UserDetailDto) userContext.get();
    }

    public static void remove() {
        userContext.remove();
    }
}
