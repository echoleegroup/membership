package com.fih.aiovpoint.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fih.aiovpoint.model.PointHistoryLog;
import com.fih.aiovpoint.model.UserPointHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class MongoLogService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MongoTemplate mongoTemplate;

    public boolean savePointLog(UserPointHistory history, boolean status, String exceptionMsg) {
        PointHistoryLog log = new PointHistoryLog();
        ObjectMapper mapper = new ObjectMapper();
        log.setCreateTime(new java.util.Date().getTime());
        log.setResourceId(history.getResourceId());
        log.setStatus(status);
        log.setMessage(exceptionMsg);
        try {
            log.setContent(mapper.writeValueAsString(history));
        }
        catch (Exception e) {
            log.setContent("Cannot covert to Json String");
        }
        mongoTemplate.save(log);
        return true;
    }
}
