package com.bjpowernode.p2p.web;

import com.bjpowernode.p2p.common.constants.Constants;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.BidUserTop;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import com.bjpowernode.p2p.service.loan.LoanInfoService;
import com.bjpowernode.p2p.service.user.FinanceAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassNmae:LoanInfoController
 * Package:com.bjpowernode.p2p.web
 * Description:
 *
 * @date:2019/3/12 001217:51
 * @author:zhang
 */
@Controller
public class LoanInfoController {

    @Autowired
    private LoanInfoService loanInfoService;

    @Autowired
    private BidInfoService bidInfoService;

    @Autowired
    private FinanceAccountService financeAccountService;
    @RequestMapping(value = "/loan/loan")
    public String loan(HttpServletRequest request, Model model,
                        @RequestParam(value="ptype",required = false) Integer ptype,
                        @RequestParam(value = "currentPage",required = false) Integer currentPage){

        if (null == currentPage) {

            currentPage = 1;
        }
        //准备参数
        Map<String,Object> paramMap = new HashMap<>();
        int pageSize = 9;
        //页码
        paramMap.put("currentPage",(currentPage-1)*pageSize);
        //每页显示的条数
        paramMap.put("pageSize",pageSize);
        //判断产品类型是否有值
        if (null != ptype) {
            paramMap.put("productType",ptype);
        }
        //分页查询产品信息列表
        PaginationVO<LoanInfo> paginationVO = loanInfoService.queryLoanInfoByPage(paramMap);
        //计算总页数
        int totalPage = paginationVO.getTotal().intValue() / pageSize;
        int mod = paginationVO.getTotal().intValue() % pageSize;
        if (mod>0){
            totalPage = totalPage + 1;
        }
        model.addAttribute("loanInfoList",paginationVO.getDataList());
        model.addAttribute("totalRows",paginationVO.getTotal());
        model.addAttribute("totalPage",totalPage);
        model.addAttribute("currentPage",currentPage);

        if (null != ptype) {
            model.addAttribute("ptype",ptype);
        }
        //查询投资排行榜
        List<BidUserTop> bidUserTopList= bidInfoService.queryBidUserTopList();
        model.addAttribute("bidUserTopList",bidUserTopList);
        return "loan";
    }
    @RequestMapping(value = "/loan/loanInfo")
    public String loanInfo(HttpServletRequest request,Model model,
                            @RequestParam(value="id",required = true) Integer id){
        //根据产品标识获取产品详情
        LoanInfo loanInfo = loanInfoService.queryLoanInfoById(id);

        //根据产品标识获取产品所有投资记录
       List<BidInfo> bidInfoList =  bidInfoService.queryBidInfoListByLoanId(id);

       model.addAttribute("loanInfo",loanInfo);
       model.addAttribute("bidInfoList",bidInfoList);
       User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        if (null != sessionUser) {
            //根据用户标识获取账户资金信息
            FinanceAccount financeAccount = financeAccountService.queryFinanceAccountByUid(sessionUser.getId());
            model.addAttribute("financeAccount",financeAccount);
        }
    return "loanInfo";
    }

}
