package com.fih.aiovpoint.service;

import com.fih.aiovpoint.mapper.ActionPointsMapper;
import com.fih.aiovpoint.mapper.UserPointHistoryMapper;
import com.fih.aiovpoint.mapper.UserPointsMapper;
import com.fih.aiovpoint.model.ActionPoints;
import com.fih.aiovpoint.model.UserPointHistory;
import com.fih.aiovpoint.model.UserPoints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class UserPointService {

    @Autowired
    UserPointsMapper userPointsMapper;

    @Autowired
    UserPointHistoryMapper userPointHistoryMapper;

    @Autowired
    ActionPointsMapper actionPointsMapper;

    @Autowired
    AccountService accountService;

    @Autowired
    MongoLogService mongoLogService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Cacheable(value="userpoints",key="#userId",unless="#result == null")
    public UserPoints getUserPoint(String userId) {
        UserPoints up = userPointsMapper.selectByUserId(userId);
        return up;
    }

    @CacheEvict(value="userpoints",key="#userId")
    @Transactional
    public UserPoints processUserPoint(String userId, String resourceType, String resourceId, String desc) throws Exception {
        ActionPoints ap = actionPointsMapper.selectByResourceType(resourceType.toUpperCase().trim());
        UserPoints up = null;
        try {
            if (ap != null) {
                up = userPointsMapper.selectByUserId(userId);
                UserPointHistory h = new UserPointHistory();
                h.setUserid(userId);
                h.setCreateat(new Date());
                h.setDescription(desc);
                h.setResourceId(resourceId);
                h.setResourceType(ap.getResourceType());
                h.setValue(ap.getValue());
                h.setType(ap.getValue()>=0 ? 1:-1);
                try {
                    int point = 0;
                    if (up != null) {
                        point = up.getTotalPoint();
                        point += ap.getValue();
                        if(point > 0) {
                            up.setPoints(point);
                            userPointsMapper.updatePoint(up);
                        }
                        else throw new Exception("Points not enough.");
                    }
                    else {
                        if(ap.getValue() < 0) throw new Exception("resourceId not enough.");
                        up = new UserPoints();
                        up.setUserId(userId);
                        up.setPoints(ap.getValue());
                        point = ap.getValue();
                        userPointsMapper.insert(up);
                    }
                    h.setCurrentTotal(point);
                    userPointHistoryMapper.insert(h);
                    mongoLogService.savePointLog(h, true, "success");
                } catch (Exception e) {
                    if(e.getMessage().contains("resourceId_UNIQUE")) throw new Exception("Point resourceId is invalid.");
                    logger.error("=== Log Points History Error === : {}, exception:{} ", h.toString(), e.getMessage());
                    mongoLogService.savePointLog(h, false, e.getMessage());
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    logger.warn("=== Log Points History Rollback ===");
                    throw e;
                }
            }
        }
        catch(Exception oe) {
            logger.error("=== processUserPoint Error === ", oe.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            logger.warn("=== processUserPoint Rollback II ===");
            throw oe;
        }
        return up;
    }

    @CacheEvict(value="userpoints",key="#userId")
    @Transactional
    public UserPoints exchangePoint(String userId, String resourceType, String resourceId, String desc, int value)  throws Exception{
        UserPoints up ;
        try {
            up = userPointsMapper.selectByUserId(userId);
            if(up == null) throw new Exception("12011:Order Insufficient points");
            UserPointHistory h = new UserPointHistory();
            h.setUserid(userId);
            h.setCreateat(new Date());
            h.setDescription(desc);
            h.setResourceId(resourceId);
            h.setResourceType(resourceType);
            h.setValue(value);
            h.setType(-1);
            try {
                int point = up.getTotalPoint();
                point += value;
                if(point >= 0) {
                    up.setPoints(point);
                    userPointsMapper.updatePoint(up);
                }
                else throw new Exception("12011:Order Insufficient points");
                h.setCurrentTotal(point);
                userPointHistoryMapper.insert(h);
                mongoLogService.savePointLog(h, true, "success");
            } catch (Exception e) {
                if(e.getMessage().contains("resourceId_UNIQUE")) throw new Exception("Point resourceId is invalid.");
                logger.error("=== Log Points History Error === : {}, exception:{} ", h.toString(), e.getMessage());
                mongoLogService.savePointLog(h, false, e.getMessage());
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                logger.warn("=== Log Points History Rollback ===");
                throw e;
            }

        }
        catch(Exception oe) {
            logger.error("=== processUserPoint Error === ", oe.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            logger.warn("=== Log Points History Rollback II ===");
            throw oe;
        }
        return up;
    }

    private HashMap<String, Object> generateUserHistoryParams(String userId, int size, int page) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        if(size > 0 & page >= 1) {
            params.put("size", size);
            params.put("offset", size*(page-1));
        }
        return params;
    }

    public List<UserPointHistory> getUserHistory(String userId, int size, int page) {
        HashMap<String, Object> params = generateUserHistoryParams(userId, size, page);
        List<UserPointHistory> history = userPointHistoryMapper.selectHistoryByUserId(params);
        return history;
    }

    public List<Integer>  getUserHistoryTotalData(String userId, int size, int page) {
        HashMap<String, Object> params = generateUserHistoryParams(userId, size, page);
        Integer total = userPointHistoryMapper.getTotalCount(params);
        List<Integer> result = new ArrayList<>();
        result.add(total);
        Double pages = Math.ceil((double)total/(double)size);
        result.add(pages.intValue());
        return result;
    }

    @Cacheable(value="actionPoint",key="#resourceType",unless="#result == null")
    public ActionPoints getActionPoint(String resourceType) {
        return actionPointsMapper.selectByResourceType(resourceType);
    }

    public ArrayList<UserPoints> getUsersPointsByAccount(String[] accountIdList) {
        ArrayList<UserPoints> up = userPointsMapper.selectAllByAccount(accountIdList);
        return up;
    }
}
