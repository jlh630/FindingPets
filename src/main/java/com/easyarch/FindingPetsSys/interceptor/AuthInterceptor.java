
package com.easyarch.FindingPetsSys.interceptor;

import com.auth0.jwt.interfaces.Claim;
import com.easyarch.FindingPetsSys.annotation.AuthPermission;
import com.easyarch.FindingPetsSys.dto.Result;
import com.easyarch.FindingPetsSys.dto.UserDetailDto;
import com.easyarch.FindingPetsSys.entity.Permission;
import com.easyarch.FindingPetsSys.exception.AuthenticationException;
import com.easyarch.FindingPetsSys.redis.RedisTokenListService;
import com.easyarch.FindingPetsSys.service.UserService;
import com.easyarch.FindingPetsSys.util.JwtUtil;
import com.easyarch.FindingPetsSys.util.UserContext;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Autowired
    private Gson gson;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTokenListService redisTokenListService;

    public AuthInterceptor() {
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        try {
            Map<String, Claim> payload = JwtUtil.verifierToken(request.getHeader("auth_token"));
            String uid = payload.get("uid").asString();
            String jti = payload.get("jti").asString();
            if (uid.isEmpty() || jti.isEmpty() || !this.redisTokenListService.existsTokenId(jti)) {
                return sendErrorResponse(response, Result.unAuthorized("token is error") );

            }
            long userId = Long.parseLong(uid);
            UserDetailDto userInfo = userService.findUserPermissionByUserId(userId);

            if (handler instanceof HandlerMethod) {
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                if (!hasPermission(handlerMethod, userInfo)) {
                    return sendErrorResponse(response, Result.ban("You don't have permission"));
                }
            }
            userInfo.setJti(jti);
            UserContext.saveUser(userInfo);
            return true;
        } catch (AuthenticationException e) {
            return sendErrorResponse(response, Result.unAuthorized("token is error"));
        }
    }

    private boolean hasPermission(HandlerMethod handlerMethod, UserDetailDto userInfo) {
        AuthPermission anno = handlerMethod.getMethodAnnotation(AuthPermission.class);
        if (anno != null) {
            Set<String> permissions = userInfo.getPermissionsList().stream()
                    .map(Permission::getPermissionName)
                    .collect(Collectors.toSet());
            return permissions.contains(anno.value());
        }
        return true;
    }

    private boolean sendErrorResponse(HttpServletResponse response, Result<String> result) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(gson.toJson(result));
        return false;
    }
}
