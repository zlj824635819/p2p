package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.common.constants.Constants;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * ClassNmae:LoanInfoServiceImpl
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 *
 * @date:2019/3/9 000921:28
 * @author:zhang
 */
@Service
public class LoanInfoServiceImpl implements LoanInfoService{
    /**
     * 序列化模板对象
     */


    @Autowired
    private LoanInfoMapper loanInfoMapper;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public Double queryHistoryAverageRate() {
        /**
         * 序列化模板对象
         */
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        Double  historyAverateRate= (Double) redisTemplate.opsForValue().get(Constants.HISTORY_AVERAGE_RATE);

        if (null == historyAverateRate) {
            //获取数据
           historyAverateRate = loanInfoMapper.selectHistoryAverageRate();

           //将数据存放到缓存中
           redisTemplate.opsForValue().set(Constants.HISTORY_AVERAGE_RATE,historyAverateRate,15, TimeUnit.MINUTES);
        }

        return historyAverateRate;
    }

    @Override
    public List<LoanInfo> queryLoanInfoListByProductType(Map<String, Object> paramMap) {
        return loanInfoMapper.selectLoanInfoByPage(paramMap);
    }

    @Override
    public PaginationVO<LoanInfo> queryLoanInfoByPage(Map<String, Object> paramMap) {
        PaginationVO<LoanInfo> paginationVO = new PaginationVO<>();

        Long total= loanInfoMapper.selectTotal(paramMap);

        List<LoanInfo> loanInfoList = loanInfoMapper.selectLoanInfoByPage(paramMap);
        //总记录数
        paginationVO.setTotal(total);

        //每页显示数据
        paginationVO.setDataList(loanInfoList);
        return paginationVO;
    }

    @Override
    public LoanInfo queryLoanInfoById(Integer id) {
        return loanInfoMapper.selectByPrimaryKey(id);
    }


}
