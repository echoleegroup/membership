package com.fih.aiovpoint.util;

import com.fih.aiovpoint.pojo.Result;

import java.util.Optional;

/*
0	Success
10001	Signature is invalid.
10002	Authorization is invalid.
10003	Request method not support.
10004	Page is invalid
10005	Size is invalid.
11001	Point type is invalid.
11002	Point amount is invalid.
11003	Point resource_type is invalid.
11004	Point resource_id is invalid.
11005	Point description is invalid.
11006	Point amount is invalid.
 */

public class ResultUtil {
    public static Result result(Integer errorCode, String message, Object result) {
        Result<Object> r = new Result<>();
        r.setErrorCode(errorCode);
        r.setMessage(message);
        Optional<Object> dataOpt = Optional.ofNullable(result);
        r.setResult(dataOpt);
        return r;
    }

    public static Result success() {
        return success(null);
    }

    public static Result success(Object data) {
        return result(0, "success", data);
    }

    public static Result success(Object data, int size, int pages) {
        Result r = result(0, "success", data);
        r.setTotalPages(pages);
        r.setTotalSizes(size);
        return r;
    }

    public static Result error(Integer code, String message) {
        return result(code, message, null);
    }

    public static Result error(String message) {
        return result(400, message, null);
    }
}
