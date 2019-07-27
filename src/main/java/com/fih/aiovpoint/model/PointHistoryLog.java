package com.fih.aiovpoint.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document(collection = "point_history_logs")
@Data
public class PointHistoryLog implements Serializable {
    private long createTime;
    private String tableName;
    private String content;
    private String resourceId;
    private boolean status;
    private String message;
}
