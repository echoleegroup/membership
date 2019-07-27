package com.fih.aiovpoint.config;

import com.fih.aiovpoint.aspect.ApiHandlerInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new ApiHandlerInterceptor())
//                .excludePathPatterns(
//                        "/api/v1/point/_generateSignature", "/api/v1/point/exchangeGoods",
//                        "/api/v1/point/addRecord", "/api/v1/point/queryByAccount",
//                        "/api/v1/point/listByAccount"
//                );
    }
}
