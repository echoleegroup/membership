package com.fih.aiovpoint.mapper;

import com.fih.aiovpoint.model.UserPointHistory;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public interface UserPointHistoryMapper {
    int insert(UserPointHistory record);

    List<UserPointHistory> selectHistoryByUserId(HashMap params);

    Integer getTotalCount(HashMap params);
}