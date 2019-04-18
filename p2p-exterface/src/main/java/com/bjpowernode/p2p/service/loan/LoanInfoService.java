package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.vo.PaginationVO;

import java.util.List;
import java.util.Map;

/**
 * ClassNmae:LoanInfoService
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 *
 * @date:2019/3/9 000920:40
 * @author:zhang
 */
public interface LoanInfoService {
    /**
     * 获取平拍投资总金额
     * @return
     */
    Double queryHistoryAverageRate();

    /**
     * 根据产品类型获取产品信息
     * @return
     */

    List<LoanInfo> queryLoanInfoListByProductType(Map<String, Object> paramMap);

    /**
     * 根据产品类型分页查询产品信息列表
     * @param paramMap
     * @return
     */
    PaginationVO<LoanInfo> queryLoanInfoByPage(Map<String, Object> paramMap);

    /**
     * 根据产品标识获取产品信息
     * @param id
     * @return
     */
    LoanInfo queryLoanInfoById(Integer id);
}
