package com.bjpowernode.p2p.web;

import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.http.HttpClientUtils;
import com.bjpowernode.p2p.common.constants.Constants;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.ResultObject;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import com.bjpowernode.p2p.service.loan.LoanInfoService;
import com.bjpowernode.p2p.service.loan.RedisService;
import com.bjpowernode.p2p.service.user.FinanceAccountService;
import com.bjpowernode.p2p.service.user.UserService;
import com.sun.beans.decoder.DocumentHandler;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassNmae:UserController
 * Package:com.bjpowernode.p2p.web
 * Description:
 *
 * @date:2019/3/13 001314:39
 * @author:zhang
 */
@Controller
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private FinanceAccountService financeAccountService;
    @Autowired
    private LoanInfoService loanInfoService;
    @Autowired
    private BidInfoService bidInfoService;
    @Autowired
    private RedisService redisService;

    @RequestMapping(value = "/loan/checkPhone")
    @ResponseBody
    public Object checkPhone(HttpServletRequest request,
                             @RequestParam(value = "phone", required = true) String phone) {
        Map<String, Object> retMap = new HashMap<>();
        //验证手机号是否重复
        User user = userService.queryUserByPhone(phone);
        if (null != user) {
            retMap.put(Constants.ERROW_MESSAGE, "该手机号码已注册，请换个手机号注册");
            return retMap;
        }
        retMap.put(Constants.ERROW_MESSAGE, "ok");
        return retMap;
    }

    @RequestMapping(value = "/loan/checkCaptcha")
    @ResponseBody
    public Map<String, Object> checkCaptcha(HttpServletRequest request,
                                            @RequestParam(value = "captcha", required = true) String captcha) {
        Map<String, Object> retMap = new HashMap<>();
        //从session中获取验证码
        String sessionCaptcha = (String) request.getSession().getAttribute(Constants.CAPTCHA);

        //用户输入的验证码与session中对比
        if (!StringUtils.equalsIgnoreCase(sessionCaptcha, captcha)) {
            retMap.put(Constants.ERROW_MESSAGE, "请输入正确的验证码");
            return retMap;
        }
        retMap.put(Constants.ERROW_MESSAGE, "ok");
        return retMap;
    }

    @RequestMapping(value = "/loan/register")
    @ResponseBody
    public Map<String, Object> register(HttpServletRequest request,
                                        @RequestParam(value = "phone", required = true) String phone,
                                        @RequestParam(value = "loginPassword", required = true) String loginPassword) {
        Map<String, Object> retMap = new HashMap<>();

        ResultObject resultObject = userService.register(phone, loginPassword);
        if (resultObject.getErrorCode().equals(Constants.SUCCESS)) {
            request.getSession().setAttribute(Constants.SESSION_USER, userService.queryUserByPhone(phone));

            retMap.put(Constants.ERROW_MESSAGE, "ok");

        } else {
            retMap.put(Constants.FAIL, "注册失败");
            return retMap;
        }


        return retMap;
    }

    @RequestMapping(value = "/loan/myFinanceAccount", method = RequestMethod.GET)
    @ResponseBody
    public FinanceAccount myFinanceAccount(HttpServletRequest request) {
        //从session中获得信息
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

        FinanceAccount financeAccount = financeAccountService.queryFinanceAccountByUid(sessionUser.getId());

        return financeAccount;
    }

    @RequestMapping(value = "/loan/verifyRealName")
    @ResponseBody
    public Object verifyRealName(HttpServletRequest request,
                                 @RequestParam(value = "realName", required = true) String realName,
                                 @RequestParam(value = "idCard", required = true) String idCard) throws Exception {
        Map<String, Object> retMap = new HashMap<>();
        //准备实名认证的参数

        Map<String, Object> paramMap = new HashMap<>();

        paramMap.put("appkey", "");
        paramMap.put("carNo", idCard);
        paramMap.put("realName", realName);
        //实名认证，调用互联网接口，该接口相应的是
        //String jsonString = HttpClientUtils.doPost("https://way.jd.com/youhuoBeijing/test", paramMap);
           String jsonString = "{\n" +
                   "    \"code\": \"10000\",\n" +
                   "    \"charge\": false,\n" +
                   "    \"remain\": 1305,\n" +
                   "    \"msg\": \"查询成功\",\n" +
                   "    \"result\": {\n" +
                   "        \"error_code\": 0,\n" +
                   "        \"reason\": \"成功\",\n" +
                   "        \"result\": {\n" +
                   "            \"realname\": \"乐天磊\",\n" +
                   "            \"idcard\": \"350721197702134399\",\n" +
                   "            \"isok\": true\n" +
                   "        }\n" +
                   "    }\n" +
                   "}";
        //解析json的字符串，使用fastjson来解析
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        //获取通信标识code
        String code = jsonObject.getString("code");
        //判断是否通信成功
        if (StringUtils.equals("10000", code)) {
            //获取真是姓名和身份证号是否匹配标签isok
            Boolean isok = jsonObject.getJSONObject("result").getJSONObject("result").getBoolean("isok");
            if (isok) {
                User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);
                //更新当前用户的信息
                User updateUser = new User();
                updateUser.setId(sessionUser.getId());
                updateUser.setName(realName);
                updateUser.setIdCard(idCard);
                int updateUserCount = userService.modifyUserById(updateUser);
                if (updateUserCount > 0) {
                    retMap.put(Constants.ERROW_MESSAGE,"ok");
                    sessionUser.setName(realName);
                    sessionUser.setIdCard(idCard);
                    request.getSession().setAttribute(Constants.SESSION_USER,sessionUser);
                } else {
                    retMap.put(Constants.ERROW_MESSAGE, "实名认证失败");
                    return retMap;
                }
            } else {
                retMap.put(Constants.ERROW_MESSAGE, "实名认证失败");
                return retMap;
            }
        } else {
            retMap.put(Constants.ERROW_MESSAGE, "实名认证失败");
            return retMap;
        }


        return retMap;
    }
    @RequestMapping(value = "/loan/loadStat")
    @ResponseBody
    public Object loanStat(HttpServletRequest request){
     Map<String,Object> retMap = new HashMap<>();
        Double historyAverageRate=loanInfoService.queryHistoryAverageRate();

        Long allUserCount = userService.queryAllUserCount();

        Double allBidMoney=bidInfoService.queryAllBidMoney();

        retMap.put("historyAverageRate",historyAverageRate);
        retMap.put("allUserCount",allUserCount);
        retMap.put("allBidMoney",allBidMoney);

        return retMap;
    }

    @RequestMapping(value ="loan/login")
    @ResponseBody
    public Object login(HttpServletRequest request,
                        @RequestParam (value = "phone" ,required = true) String phone,
                        @RequestParam (value = "loginPassword",required = true ) String loginPassword){
        Map<String,Object> retMap = new HashMap<>();
        //用户登录，【1根据手机号和登录密码查询用户2更新最近登录时间】
        User user=userService.login(loginPassword,phone);
        if (null == user) {
            retMap.put(Constants.ERROW_MESSAGE,"用户名或密码输入错误");
            return retMap;
        }
        retMap.put(Constants.ERROW_MESSAGE,"ok");
        //将用户信息存到session中
        request.getSession().setAttribute(Constants.SESSION_USER,user);
    return retMap;
    }
    @RequestMapping(value = "/loan/logout")
    public String logout(HttpServletRequest request){
        //让session失效
        request.getSession().invalidate();

        return "redirect:/index";

    }
    @RequestMapping(value = "loan/sendMessageCode")
    @ResponseBody
    public Object sendMessageCode(HttpServletRequest request,
                                  @RequestParam(value = "phone",required = true) String phone) throws Exception {

        Map<String,Object> retMap = new HashMap<>();
        //随机生成一个数字
        String messageCode = this.getRandomNumber(4);
        //准备发送短信的模板
        String content = "【凯信通】您的验证码为"+messageCode;
        //发送短信验证码，调用京东云平台的短信api接口：106短信api
        Map<String ,Object> paramMap = new HashMap<>();
        paramMap.put("appkey","fc8578e3dc7ac1e826b29393bb23b156");
        paramMap.put("mobile","phone");
        paramMap.put("content",content);
       //String  jsonResult= HttpClientUtils.doPost("https://way.jd.com/kaixintong/kaixintong",paramMap);
        String jsonResult="{\"code\":\"10000\",\"charge\":false,\"remain\":0,\"msg\":\"查询成功\",\"result\":\"<?xml version=\\\"1.0\\\" encoding=\\\"utf-8\\\" ?><returnsms>\\n <returnstatus>Success</returnstatus>\\n <message>ok</message>\\n <remainpoint>-664529</remainpoint>\\n <taskID>83289479</taskID>\\n <successCounts>1</successCounts></returnsms>\"}\n";
        JSONObject jsonObject = JSONObject.parseObject(jsonResult);

        String code = jsonObject.getString("code");
        if (StringUtils.equals("10000",code)){
            String resultXml = jsonObject.getString("result");

           Document document= DocumentHelper.parseText(resultXml);
          Node node = document.selectSingleNode("//returnstatus");
                  String text = node.getText();
          //判断是否发送成功StringUtils.equals(text,"Success")???
            if (StringUtils.equals(text,"Success")){
                    redisService.put(phone,messageCode);
                    retMap.put("messageCode",messageCode);
                    retMap.put(Constants.ERROW_MESSAGE,"ok");
            }else {
                retMap.put(Constants.ERROW_MESSAGE,"短信发送失败");
                return false;
            }
        }else{
            retMap.put(Constants.ERROW_MESSAGE,"通信失败");
            return retMap;
        }

        return retMap;
    }
    private String getRandomNumber(int count){
        String args[] = {"1","2","3","4","5","6","7","8","9","0"};

        StringBuilder stringBuilder = new StringBuilder();

        for (int i=0;i<count;i++){
            int index = (int) Math.round(Math.random()*9);
            stringBuilder.append(args[index]);
        }
        return stringBuilder.toString();
    }
    @RequestMapping(value = "/loan/myCenter")
    public String myCenter(HttpServletRequest request, Model model){
       User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        //根据用户标识获取账户可用余额
       FinanceAccount financeAccount = financeAccountService.queryFinanceAccountByUid(sessionUser   .getId());
        //根据用户标识获取最近的投资记录
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("uid",sessionUser.getId());
        paramMap.put("currentPage",0);
        paramMap.put("pageSize",5);

        List<BidInfo> bidInfoList = bidInfoService.queryBidInfoListByID(paramMap);
        //根据用户表示获取最近的充值记录
        //根据用户标识获取最近的收益记录
        model.addAttribute("financeAccount",financeAccount);
        model.addAttribute("bidInfoList",bidInfoList);
        return "myCenter";
    }
}
