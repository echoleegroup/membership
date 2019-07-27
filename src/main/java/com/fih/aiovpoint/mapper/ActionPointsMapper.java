package com.fih.aiovpoint.mapper;

import com.fih.aiovpoint.model.ActionPoints;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActionPointsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ActionPoints record);

    ActionPoints selectByPrimaryKey(Integer id);

    List<ActionPoints> selectAll();

    int updateByPrimaryKey(ActionPoints record);

    ActionPoints selectByResourceType(String resourceType);
}