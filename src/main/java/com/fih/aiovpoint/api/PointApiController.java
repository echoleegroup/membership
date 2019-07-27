package com.fih.aiovpoint.api;

import com.fih.aiovpoint.model.UserPointHistory;
import com.fih.aiovpoint.model.UserPoints;
import com.fih.aiovpoint.pojo.AccountProfile;
import com.fih.aiovpoint.pojo.Result;
import com.fih.aiovpoint.service.AccountService;
import com.fih.aiovpoint.util.ResultUtil;
import com.fih.aiovpoint.service.UserPointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1/point")
public class PointApiController {

    @Autowired
    UserPointService userPointService;
    @Autowired
    AccountService accountService;

    private static Logger logger = LoggerFactory.getLogger(PointApiController.class);


    @GetMapping(value = "/query")
    public Result getPoint(HttpServletRequest request){
        String auth = request.getHeader("Authorization");
        AccountProfile profile = getAccount(auth);
        if(profile != null) {
            UserPoints result = userPointService.getUserPoint(profile.getAccount());
            return ResultUtil.success(result);
        }
        else return ResultUtil.error(10002, "Authorization is invalid.");
    }

    @GetMapping(value = "/list")
    public Result getList(HttpServletRequest request){
        int page = 1, size = 30;
        String auth = request.getHeader("Authorization");
        AccountProfile profile = getAccount(auth);
        if(profile != null) {
            try {
                page = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : page;
                size = request.getParameter("size") != null ? Integer.parseInt(request.getParameter("size")) : size;
            }
            catch(NumberFormatException e) {
                return ResultUtil.error(10004, e.getMessage());
            }
            List<UserPointHistory> history = userPointService.getUserHistory(profile.getAccount(), size, page);
            List<Integer> total = userPointService.getUserHistoryTotalData(profile.getAccount(), size, page);
            return ResultUtil.success(history, total.get(0), total.get(1));
        }
        else return ResultUtil.error(10002, "Authorization is invalid.");
    }


    private AccountProfile getAccount(String token) {
        AccountProfile account = accountService.getAccountProfile(token);
        return account;
    }

}
