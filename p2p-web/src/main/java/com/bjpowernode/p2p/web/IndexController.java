package com.bjpowernode.p2p.web;

import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import com.bjpowernode.p2p.service.loan.LoanInfoService;
import com.bjpowernode.p2p.common.constants.Constants;
import com.bjpowernode.p2p.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * ClassNmae:IndexController
 * Package:com.bjpowernode.p2p.web
 * Description:
 *
 * @date:2019/3/9 000921:45
 * @author:zhang
 */
@Controller
public class IndexController {

    @Autowired
    private LoanInfoService loanInfoService;
    @Autowired
    private UserService userService;
    @Autowired
    private BidInfoService bidInfoService;

    @RequestMapping(value = "/index")
    public String index(HttpServletRequest request, Model model){
        //查询历史年化收益率
        Double historyAverageRate=loanInfoService.queryHistoryAverageRate();
        model.addAttribute(Constants.HISTORY_AVERAGE_RATE,historyAverageRate);
        //平台注册总人数
        Long allUserCount=userService.queryAllUserCount();

        model.addAttribute(Constants.ALL_USER_COUNT,allUserCount);
        //累计投资金额
        Double allBidMoney = bidInfoService.queryAllBidMoney();
        model.addAttribute(Constants.ALL_BID_MONEY,allBidMoney);

        //准备查询参数
        Map<String,Object> paramMap = new HashMap();
        paramMap.put("currentPage",0);


        //新手宝：显示第一页，每页显示一条，产品类型0
        paramMap.put("productType", Constants.PRODUCT_TYPE_X);
        paramMap.put("pageSize",1);
        List<LoanInfo> xLoanInfoList = loanInfoService.queryLoanInfoListByProductType(paramMap);
        model.addAttribute("xLoanInfoList",xLoanInfoList);
        //优选产品：显示第一页，每页显示四条，产品类型1
        paramMap.put("productType", Constants.PRODUCT_TYPE_U);
        paramMap.put("pageSize",4);
        List<LoanInfo> uLoanInfoList = loanInfoService.queryLoanInfoListByProductType(paramMap);
        model.addAttribute("uLoanInfoList",uLoanInfoList);
        //散标：显示第一页，每页显示八条，产品类型2
        paramMap.put("productType", Constants.PRODUCT_TYPE_S);
        paramMap.put("pageSize",8);
        List<LoanInfo> sLoanInfoList = loanInfoService.queryLoanInfoListByProductType(paramMap);
        model.addAttribute("sLoanInfoList",sLoanInfoList);
        return "index";
    }
}
