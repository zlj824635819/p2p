package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.loan.RechargeRecord;

/**
 * ClassNmae:RechargeRecordService
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 *
 * @date:2019/3/23 002311:54
 * @author:zhang
 */
public interface RechargeRecordService {
    /**
     * 新增充值记录
     * @param rechargeRecord
     * @return
     */
    int addRechargeRecord(RechargeRecord rechargeRecord);
}
