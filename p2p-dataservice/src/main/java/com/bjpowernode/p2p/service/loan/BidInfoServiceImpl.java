package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.common.constants.Constants;
import com.bjpowernode.p2p.mapper.loan.BidInfoMapper;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.vo.BidUserTop;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.model.vo.ResultObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * ClassNmae:BidInfoServiceImpl
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 *
 * @date:2019/3/11 001118:01
 * @author:zhang
 */
@Service
public class BidInfoServiceImpl implements BidInfoService {
    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    @Autowired
    private LoanInfoMapper loanInfoMapper;

    @Autowired
    private BidInfoMapper bidInfoMapper;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    @Override
    public Double queryAllBidMoney() {

        Double allBidMoney= (Double) redisTemplate.opsForValue().get(Constants.ALL_BID_MONEY);
        if (null == allBidMoney) {
            //取数据库查询并存到redis缓存中
           allBidMoney =bidInfoMapper.selectAllBidMoney();

           redisTemplate.opsForValue().set(Constants.ALL_BID_MONEY,allBidMoney,15, TimeUnit.MINUTES);
        }
        return allBidMoney;
    }

    @Override
    public List<BidInfo> queryBidInfoListByLoanId(Integer loanId) {
        return bidInfoMapper.selectBidInfoListByLoanId(loanId);
    }

    @Override
    public List<BidInfo> queryBidInfoListByID(Map<String, Object> paramMap) {

        return bidInfoMapper.selectBidInfoListByPage(paramMap);
    }

    @Override
    public PaginationVO<BidInfo> queryBidInfoListByPage(Map<String, Object> paramMap) {
        PaginationVO<BidInfo> paginationVO = new PaginationVO<>();

        paginationVO.setTotal(bidInfoMapper.selectTotal(paramMap));
        paginationVO.setDataList(bidInfoMapper.selectBidInfoListByPage(paramMap));
        return paginationVO;
    }

    @Override
    public ResultObject invest(Map<String, Object> paramMap) {
        ResultObject resultObject = new ResultObject();

        resultObject.setErrorCode(Constants.SUCCESS);

        Integer loanId = (Integer) paramMap.get("loanId");
        Integer uid = (Integer) paramMap.get("uid");
        Double bidMoney = (Double) paramMap.get("bidMoney");
        String phone = (String) paramMap.get("phone");

        //更新产品声誉可投金额
        //使用数据库乐观锁来解决超卖
        LoanInfo loanInfo = loanInfoMapper.selectByPrimaryKey(loanId);
        //获取产品的版本号
        paramMap.put("version",loanInfo.getVersion());
        int updataLeftProductMoneyCount=loanInfoMapper.updataLeftProductMoneyByLoanId(paramMap);
        if (updataLeftProductMoneyCount>0){
        //更新账户可用余额
           int updateFinanceAccountCount = financeAccountMapper.updateFinanceAccountByInvest(paramMap);
           if (updataLeftProductMoneyCount>0){
            //新增投资记录
               BidInfo bidInfo = new BidInfo();
               bidInfo.setUid(uid);
               bidInfo.setBidMoney(bidMoney);
               bidInfo.setLoanId(loanId);
               bidInfo.setBidTime(new Date());
               bidInfo.setBidStatus(1);

               int insertSelective = bidInfoMapper.insertSelective(bidInfo);
               if (insertSelective>0){
                   //查询产品剩余可投金额
                   LoanInfo loanDetail=loanInfoMapper.selectByPrimaryKey(loanId);
                   if (0==loanDetail.getLeftProductMoney()){
                       //更新产品状态及满标时间
                       LoanInfo updataLoanInfo = new LoanInfo();
                       updataLoanInfo.setId(loanId);
                       updataLoanInfo.setProductStatus(1);
                       updataLoanInfo.setProductFullTime(new Date());
                       int count = loanInfoMapper.updateByPrimaryKeySelective(updataLoanInfo);
                       if (count<=0){
                           resultObject.setErrorCode(Constants.FAIL);
                       }
                   }

                   //将用户投资金额放到redis缓存中
                   redisTemplate.opsForZSet().incrementScore(Constants.INVEST_TOP,phone,bidMoney);
        //新增投资记录
               }else {
            resultObject.setErrorCode(Constants.FAIL);
               }
           }else {
            resultObject.setErrorCode(Constants.FAIL);
           }
        }else {
            resultObject.setErrorCode(Constants.FAIL);
        }
        //查看产品剩余可投金额
        return resultObject;
    }

    @Override
    public List<BidUserTop> queryBidUserTopList() {

        List<BidUserTop> bidUserTopList = new ArrayList<>();

       Set<ZSetOperations.TypedTuple<Object>> typedTuples = redisTemplate.opsForZSet().reverseRangeWithScores(Constants.INVEST_TOP, 0, 5);

        Iterator<ZSetOperations.TypedTuple<Object>> iterator = typedTuples.iterator();

        while (iterator.hasNext()){
            ZSetOperations.TypedTuple<Object> next = iterator.next();
            String phone = (String) next.getValue();
            Double score = next.getScore();

            BidUserTop bidUserTop = new BidUserTop();
            bidUserTop.setPhone(phone);
            bidUserTop.setScore(score);

            bidUserTopList.add(bidUserTop);
        }
        return bidUserTopList;
    }

}
