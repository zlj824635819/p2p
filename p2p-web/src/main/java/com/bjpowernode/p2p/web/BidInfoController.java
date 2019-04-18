package com.bjpowernode.p2p.web;

import com.bjpowernode.p2p.common.constants.Constants;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.model.vo.ResultObject;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassNmae:BidInfoController
 * Package:com.bjpowernode.p2p.web
 * Description:
 *
 * @date:2019/3/16 001621:16
 * @author:zhang
 */
@Controller
public class BidInfoController {
    @Autowired
    private BidInfoService bidInfoService;


    @RequestMapping(value = "/loan/myInvest")
    public String myInvest(HttpServletRequest request, Model model,
                           @RequestParam (value = "currentPage",required = false) Integer currntPage){
        if (null == currntPage) {
            currntPage = 1;
        }
        User sessionUser= (User) request.getSession().getAttribute(Constants.SESSION_USER);
        //准备查询参数
        int pageSize=5;
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("uid",sessionUser.getId());
        paramMap.put("currentPage",(currntPage-1)*pageSize);
        paramMap.put("pageSize",pageSize);
        //根据用户标识分页查询投资信息列表（用户标识，当前页码，每页显示条数) -> 返回PageinationVO(总记录数,每页显示的条数)
        PaginationVO<BidInfo> paginationVO = bidInfoService.queryBidInfoListByPage(paramMap);

        //计算总页数
        int totalPage = paginationVO.getTotal().intValue() / pageSize;
        int mod = paginationVO.getTotal().intValue() % pageSize;
        if (mod>0){
            totalPage = totalPage + 1;
        }

        model.addAttribute("bidInfoList",paginationVO.getDataList());
        model.addAttribute("totalPage",totalPage);
        model.addAttribute("totalRows",paginationVO.getTotal());
        model.addAttribute("currentPage",currntPage);
        return "myInvest";

    }
    @RequestMapping(value = "/loan/invest")
    @ResponseBody
    public  Object invest(HttpServletRequest request,
                                      @RequestParam(value = "loanId",required = true) Integer loanId,
                                      @RequestParam(value = "bidMoney",required = true) Double bidMoney){
        Map<String,Object> retMap = new HashMap<>();
        //从session中获取用户信息
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

        //准备投资参数
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("uid",sessionUser.getId());
        paramMap.put("loanId",loanId);
        paramMap.put("bidMoney",bidMoney);
        paramMap.put("phone",sessionUser.getPhone());
        //用户投资
        ResultObject resultObject = bidInfoService.invest(paramMap);

        if (StringUtils.equals(Constants.SUCCESS,resultObject.getErrorCode())){
                retMap.put(Constants.ERROW_MESSAGE,"ok");
        }else {
            retMap.put(Constants.ERROW_MESSAGE,"投资失败");
            return retMap;
        }
        return retMap;
    }
}
