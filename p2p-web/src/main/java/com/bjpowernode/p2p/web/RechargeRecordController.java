package com.bjpowernode.p2p.web;

import com.bjpowernode.p2p.common.constants.Constants;
import com.bjpowernode.p2p.common.util.DateUtils;
import com.bjpowernode.p2p.model.loan.RechargeRecord;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.service.loan.RechargeRecordService;
import com.bjpowernode.p2p.service.loan.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * ClassNmae:RechargeRecordController
 * Package:com.bjpowernode.p2p.web
 * Description:
 *
 * @date:2019/3/23 00239:49
 * @author:zhang
 */
@Controller
public class RechargeRecordController {

    @Autowired
    private RechargeRecordService rechargeRecordService;

    @Autowired
    private RedisService redisService;
    @RequestMapping(value = "/loan/toAlipayRecharge")
    public String  toAlipayRacharge(HttpServletRequest request, Model model,
                                    @RequestParam(value = "rechargeMoney",required = true) Double rechargeMoney){
        System.out.println("阿里pay");
        User sessionuser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

        //生成充值记录
        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setUid(sessionuser.getId());
        rechargeRecord.setRechargeMoney(rechargeMoney);
        //生成一个全局唯一的订单编号
        String rechargeNo= DateUtils.getTimestamp()+redisService.getOnlyNumber();
        rechargeRecord.setRechargeNo(rechargeNo);
        rechargeRecord.setRechargeTime(new Date());
        rechargeRecord.setRechargeStatus("0");
        rechargeRecord.setRechargeDesc("支付宝充值");
        int addRechargeCount=rechargeRecordService.addRechargeRecord(rechargeRecord);
        if (addRechargeCount>0){
            //调用向pay传递支付请求需要的参数

        }else {
            //失败
            model.addAttribute("trade_msg","充值失败，请稍后重试");
            return "toRechargeBack";
        }
        return "";

    }

    @RequestMapping(value = "/loan/toWxpayRecharge")
    public void toWxpayRecharge(HttpServletRequest request,
                                @RequestParam(value = "rechargeMoney",required = true)Double rechargeMoney){
        System.out.println("weixinpay");
    }
}
