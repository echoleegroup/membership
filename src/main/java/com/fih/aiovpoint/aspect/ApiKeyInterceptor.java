package com.fih.aiovpoint.aspect;


import com.fih.aiovpoint.constant.ApiKeyConstant;
import com.fih.aiovpoint.exception.ApiException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Aspect
@Configuration
public class ApiKeyInterceptor {
    private Logger logger = LoggerFactory.getLogger(ApiKeyInterceptor.class);

    @Autowired
    private HttpSession session; // 直接注入
    @Autowired
    private HttpServletRequest request;

    @Pointcut("execution(public * com.fih.aiovpoint.api.PointApiForServerController.*(..))")
    public void apiKeyCheck() {
    }

    @Before(value = "apiKeyCheck()")
    public void doBefore(JoinPoint joinPoint) {
        String key = StringUtils.isEmpty(request.getHeader("key")) ? "":request.getHeader("key");
        if(StringUtils.isEmpty(key)) throw new ApiException(11007,"Point api key is invalid");
        else {
            boolean verified = false;
            for(ApiKeyConstant.ServiceKey k : ApiKeyConstant.ServiceKey.values()) {
                if(key.equals(k.getValue())) {
                    verified = true;
                    break;
                }
            }
            if(!verified) throw new ApiException(11007,"Point api key is invalid");
        }
    }
}
