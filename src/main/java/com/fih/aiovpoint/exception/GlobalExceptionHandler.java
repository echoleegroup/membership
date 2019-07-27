package com.fih.aiovpoint.exception;

import com.fih.aiovpoint.pojo.Result;
import com.fih.aiovpoint.util.ResultUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@RestController
public class GlobalExceptionHandler {
    public static final String DEFAULT_ERROR_VIEW = "error";

    @ExceptionHandler(value = Exception.class)
    public Result defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        Result result = new Result();
        result.setErrorCode(99999);
        result.setMessage(e.getMessage());
        return result;
    }

    @ExceptionHandler(value=ApiException.class)
    @ResponseBody
    public Result apiErrorHandler(HttpServletRequest req, ApiException ex) throws Exception {
        return ResultUtil.error(ex.getCode(), ex.getDescription());
    }


}
