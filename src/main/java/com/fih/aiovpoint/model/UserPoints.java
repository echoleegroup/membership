package com.fih.aiovpoint.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserPoints implements Serializable {

    private static final long serialVersionUID = -2964579527491244416L;

    private String userId;

    @JsonIgnore
    private Integer points;

    @JsonIgnore
    private Date updateat;

    //for api result
    @Getter(value = AccessLevel.NONE)
    private Integer totalPoint;


    public Integer getTotalPoint(){ return this.points;}

}