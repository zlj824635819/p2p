package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.vo.BidUserTop;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.model.vo.ResultObject;

import java.util.List;
import java.util.Map;

/**
 * ClassNmae:BidInfoService
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 *
 * @date:2019/3/11 001118:00
 * @author:zhang
 */
public interface BidInfoService {


    /**
     * 获取平台的投资总额
     * @return
     */
    Double queryAllBidMoney();

    /**
     * 根据产品标识获取产品所有的投资记录(包含用户信息)
     * @param loanId
     * @return
     */
    List<BidInfo> queryBidInfoListByLoanId(Integer loanId);

    /**
     * 根据用户表示获取最近用户投资记录
     * @param paramMap
     * @return
     */
    List<BidInfo> queryBidInfoListByID(Map<String, Object> paramMap);

    /**
     * 根据用户标识分页查询投资记录
     * @param paramMap
     * @return
     */
    PaginationVO<BidInfo> queryBidInfoListByPage(Map<String, Object> paramMap);

    /**
     * 用户投资
     * @param paramMap
     * @return
     */
    ResultObject invest(Map<String, Object> paramMap);

    /**
     * 获取用户投资排行榜
     * @return
     */
    List<BidUserTop> queryBidUserTopList();
}
