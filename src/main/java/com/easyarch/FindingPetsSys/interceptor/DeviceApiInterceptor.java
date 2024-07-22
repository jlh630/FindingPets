package com.easyarch.FindingPetsSys.interceptor;

import cn.hutool.core.util.StrUtil;
import com.easyarch.FindingPetsSys.dto.Result;
import com.google.gson.Gson;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class DeviceApiInterceptor implements HandlerInterceptor {
    @Autowired
    private Gson gson;
    private static final String KEY = "DeviceKey";

    public DeviceApiInterceptor() {
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String key = request.getHeader("key");
        if (!StrUtil.hasEmpty(key) && StrUtil.equals(KEY, key)) {
            return true;
        } else {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(this.gson.toJson(Result.error("key is error!")));
            return false;
        }
    }
}
