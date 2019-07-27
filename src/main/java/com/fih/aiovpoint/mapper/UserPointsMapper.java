package com.fih.aiovpoint.mapper;

import com.fih.aiovpoint.model.UserPoints;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface UserPointsMapper {
    int insert(UserPoints record);

    List<UserPoints> selectAll();

    ArrayList<UserPoints> selectAllByAccount(String[] accountId);

    UserPoints selectByUserId(String userId);

    void updatePoint(UserPoints record);
}