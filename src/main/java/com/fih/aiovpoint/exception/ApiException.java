package com.fih.aiovpoint.exception;

import lombok.Data;

@Data
public class ApiException extends RuntimeException {

    private Integer code;
    private String description;

    public ApiException() {

    }

    public ApiException(Integer code) {
        this.code = code;
    }

    public ApiException(String description) {
        this.code = 9999;
        this.description = description;
    }

    public ApiException(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
}
