package com.fih.aiovpoint.api;

import com.fih.aiovpoint.model.ActionPoints;
import com.fih.aiovpoint.model.UserPoints;
import com.fih.aiovpoint.model.UserPointHistory;
import com.fih.aiovpoint.pojo.Result;
import com.fih.aiovpoint.service.AccountService;
import com.fih.aiovpoint.service.UserPointService;
import com.fih.aiovpoint.util.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/point")
public class PointApiForServerController {

    @Autowired
    UserPointService userPointService;
    @Autowired
    AccountService accountService;

    private static Logger logger = LoggerFactory.getLogger(PointApiForServerController.class);

    @PostMapping(value = "/addRecord")
    public Result addPoint(@RequestBody final Map<String, Object> params, HttpServletRequest request){
        String userId = params.get("userId")!=null ? params.get("userId").toString():null;
        String resourceType = params.get("resourceType")!=null ? params.get("resourceType").toString():null;
        String resourceId = params.get("resourceId")!=null ? params.get("resourceId").toString():null;
        String desc = params.get("description")!=null ? params.get("description").toString():null;
        if(StringUtils.isEmpty(resourceId)) return ResultUtil.error(11004, "Point resourceId is invalid.");
        ActionPoints ap = userPointService.getActionPoint(resourceType);
        if(ObjectUtils.isEmpty(ap)) return ResultUtil.error(11003, "Point resourceType is invalid.");
        try {
            userPointService.processUserPoint(userId, resourceType, resourceId, desc);
        }
        catch(Exception e){
            if(e.getMessage().contains("Point resourceId is invalid.")) return ResultUtil.error(11004, "Point resourceId is invalid.");
            logger.error("==== addPoint Exception ====", e);
            return ResultUtil.error(99999, e.getMessage());
        }
        return ResultUtil.success();
    }

    @PostMapping(value="/exchangeGoods")
    public Result exchangeGoods(@RequestBody final Map<String, Object> params, HttpServletRequest request) {
        int amount = ObjectUtils.isEmpty(params.get("amount")) ? 0:Integer.parseInt(params.get("amount").toString());
        String userId = ObjectUtils.isEmpty(params.get("userId")) ? "" : params.get("userId").toString();
        String resourceType = ObjectUtils.isEmpty(params.get("resourceType")) ? "" : params.get("resourceType").toString();
        String resourceId = ObjectUtils.isEmpty(params.get("resourceId")) ? "" : params.get("resourceId").toString();
        String desc = ObjectUtils.isEmpty(params.get("description")) ? "" : params.get("description").toString();
//        if(amount >=0) return ResultUtil.error(11002, "Point amount is invalid."); //若預期兌換給負值，若之後有退貨問題，應拿掉這個檢核
        if(!resourceType.equalsIgnoreCase("D-02")) return ResultUtil.error(11003, "Point resourceType is invalid.");
        if(StringUtils.isEmpty(resourceId)) return ResultUtil.error(11004, "Point resourceId is invalid.");
        try {
            userPointService.exchangePoint(userId, resourceType, resourceId, desc, amount);
        }
        catch(Exception e){
            if(e.getMessage().contains("Point resourceId is invalid.")) return ResultUtil.error(11004, "Point resourceId is invalid.");
            if(e.getMessage().contains("12011")) return ResultUtil.error(12011, "Order Insufficient points.");
            else return ResultUtil.error(99999, e.getMessage());
        }
        return ResultUtil.success();
    }

    @GetMapping(value="/_generateSignature")
    public Result generateSignature(HttpServletRequest request) {
        return ResultUtil.success(accountService.getSignature());
    }


    @PostMapping(value = "/queryByAccount")
    public Result getPointByAccount(@RequestBody final Map<String, String[]> params, HttpServletRequest request) {
        String[] accountList = ObjectUtils.isEmpty(params.get("account")) ? null : params.get("account");
        if (accountList == null) return ResultUtil.error(99999, "Account is empty.");

        ArrayList<UserPoints> result = userPointService.getUsersPointsByAccount(accountList);
        return ResultUtil.success(result);
    }

    @GetMapping(value = "/listByAccount")
    public Result getList(HttpServletRequest request){
        int page = 1, size = 30;
        String account = request.getParameter("account")!=null ? request.getParameter("account").toString() : "";
        if(!StringUtils.isEmpty(account)) {
            try {
                page = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : page;
                size = request.getParameter("size") != null ? Integer.parseInt(request.getParameter("size")) : size;
            }
            catch(NumberFormatException e) {
                return ResultUtil.error(10004, e.getMessage());
            }
            List<UserPointHistory> history = userPointService.getUserHistory(account, size, page);
            List<Integer> total = userPointService.getUserHistoryTotalData(account, size, page);
            return ResultUtil.success(history, total.get(0), total.get(1));
        }
        else return ResultUtil.error(11006, "Point account is invalid.");

    }

}
