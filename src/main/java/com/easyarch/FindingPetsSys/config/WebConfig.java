
package com.easyarch.FindingPetsSys.config;

import com.easyarch.FindingPetsSys.interceptor.AuthInterceptor;
import com.easyarch.FindingPetsSys.interceptor.DeviceApiInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private AuthInterceptor authInterceptor;
    @Autowired
    private DeviceApiInterceptor deviceApiInterceptor;

    public WebConfig() {
    }
    /**
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins(new String[]{"*"}).allowedMethods(new String[]{"*"}).allowedHeaders(new String[]{"*"});
    }**/

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.authInterceptor).addPathPatterns("/**").excludePathPatterns( "/login/**", "/orders/notify", "/captcha/login","/captcha/register","/register", "/locations");
        registry.addInterceptor(this.deviceApiInterceptor).addPathPatterns("/locations");
    }
}
