package com.fih.aiovpoint.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Data
public class UserPointHistory implements Serializable {
    public UserPointHistory() {
        UUID uuid = UUID.randomUUID();
        this.setRequestid(uuid.toString());
    }

    @JsonIgnore
    private String requestid;

    @JsonIgnore
    private String userid;

    @JsonIgnore
    private Date createat;

    @JsonIgnore
    private Integer type;

    @JsonIgnore
    private Integer value;

    private Integer currentTotal;

    private String resourceType;

    private String resourceId;

    private String description;

    //for api result
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    private Integer amount;
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    private Long createdTime;

    public Integer getAmount(){ return this.value;}

    public Long getCreatedTime() { return this.createat.getTime();}

}