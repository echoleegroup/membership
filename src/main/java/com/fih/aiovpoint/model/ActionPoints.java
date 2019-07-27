package com.fih.aiovpoint.model;

import lombok.Data;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Data
@Repository
public class ActionPoints implements Serializable {
    private Integer id;

    private String resourceType;

    private String module;

    private String function;

    private Integer value;

}