package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.common.constants.Constants;
import com.bjpowernode.p2p.common.util.DateUtils;
import com.bjpowernode.p2p.mapper.loan.BidInfoMapper;
import com.bjpowernode.p2p.mapper.loan.IncomeRecordMapper;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.IncomeRecord;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassNmae:IncomeRecordService
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 *
 * @date:2019/3/18 001817:34
 * @author:zhang
 */
@Service
public class IncomeRecordServiceImpl  implements IncomeRecordService{


    @Autowired
    private LoanInfoMapper loanInfoMapper;

    @Autowired
    private BidInfoMapper bidInfoMapper;
    @Autowired
    private IncomeRecordMapper incomeRecordMapper;
    @Autowired
    private FinanceAccountMapper financeAccountMapper;
    @Override
    public void generateIncomePlan() {


        //获取所有已满标的产品 -》 返回list(已满标的产品)
        List<LoanInfo> loanInfoList=loanInfoMapper.selectLoanInfoByProductStatus(1);
        //循环遍历，获取每一个已满标的产品
        for (LoanInfo loanInfo : loanInfoList) {
            //获取当前所有的投资记录 - >返回List<投资记录>
           List<BidInfo> bidInfoList = bidInfoMapper.selectBidInfoByLoanId(loanInfo.getId());
            //循环遍历，获取到每一条投资记录
            for (BidInfo bidInfo:bidInfoList){
                //将每一条投资记录生成相应的收益计划
                IncomeRecord incomeRecord = new IncomeRecord();

                incomeRecord.setUid(bidInfo.getUid());
                incomeRecord.setBidId(bidInfo.getId());
                incomeRecord.setBidMoney(bidInfo.getBidMoney());
                incomeRecord.setIncomeStatus(0);
                incomeRecord.setLoanId(loanInfo.getId());
                Date incomeDate = null;

                //收益金额 = 投资金额*天利率*投资天数
                Double incomeMoney = null;
                if (Constants.PRODUCT_TYPE_X == loanInfo.getProductType()){

                  incomeDate =  DateUtils.getDateByAddDays(loanInfo.getProductFullTime(), loanInfo.getCycle());
                  incomeMoney = bidInfo.getBidMoney()*(loanInfo.getRate()/100*365)*loanInfo.getCycle();
                }else {
                  incomeDate = DateUtils.getDateByAddMonths(loanInfo.getProductFullTime(), loanInfo.getCycle());
                    incomeMoney = bidInfo.getBidMoney()*(loanInfo.getRate()/100*365)*loanInfo.getCycle()*30;
                }
                incomeMoney = Math.round(incomeMoney*Math.pow(10,2))/Math.pow(10,2);
                incomeRecord.setIncomeMoney(incomeMoney);
                incomeRecord.setIncomeDate(incomeDate);
                incomeRecordMapper.insertSelective(incomeRecord);
            }

            //更新当前的产品状态并且生成收益计划
            LoanInfo updateLoanInfo = new LoanInfo();
            updateLoanInfo.setId(loanInfo.getId());
            updateLoanInfo.setProductStatus(2);
            loanInfoMapper.updateByPrimaryKeySelective(updateLoanInfo);
        }


    }

    @Override
    public void generateIncomeBack() {
        //获取满足收益时间与当前时间相等且收益为0未返回的收益记录 -》返回list收益记录
        List<IncomeRecord> incomeRecordList=incomeRecordMapper.selectIncomeRecordByIncomeStatusAndCurDate(0);

        //循环遍历，获取每一条收益记录
        for (IncomeRecord incomeRecord:incomeRecordList){
            Map<String,Object> paramMap = new HashMap<>();
            paramMap.put("uid",incomeRecord.getUid());
            paramMap.put("bidMoney",incomeRecord.getBidMoney());
            paramMap.put("incomeMoney",incomeRecord.getIncomeMoney());
            //将收益及本金返还给指定的用户
           int updateCount = financeAccountMapper.updateFinanceAccountByIncomeBack(paramMap);

           if (updateCount>0){
            //更新当前的收益记录为已返还
               IncomeRecord updateIncomeRecord = new IncomeRecord();
               updateIncomeRecord.setId(incomeRecord.getId());
               updateIncomeRecord.setIncomeStatus(1);
                incomeRecordMapper.updateByPrimaryKeySelective(updateIncomeRecord);
           }

        }




    }
}
