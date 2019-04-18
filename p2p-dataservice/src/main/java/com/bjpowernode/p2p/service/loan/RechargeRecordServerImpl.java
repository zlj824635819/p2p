package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.mapper.loan.RechargeRecordMapper;
import com.bjpowernode.p2p.model.loan.RechargeRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ClassNmae:RechargeRecordServerImpl
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 *
 * @date:2019/3/23 002311:56
 * @author:zhang
 */
@Service
public class RechargeRecordServerImpl implements RechargeRecordService {

    @Autowired
    private RechargeRecordMapper rechargeRecordMapper;

    @Override
    public int addRechargeRecord(RechargeRecord rechargeRecord) {

        return rechargeRecordMapper.insertSelective(rechargeRecord);
    }
}
