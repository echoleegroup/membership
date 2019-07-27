package com.fih.aiovpoint.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

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

@Data
public class Result<T> {
    private Integer errorCode;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Optional<T> result;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer totalPages;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer totalSizes;
}
