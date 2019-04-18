package com.bjpowernode.p2p.service.user;

import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ClassNmae:FinanceAccountServiceImpl
 * Package:com.bjpowernode.p2p.service.user
 * Description:
 *
 * @date:2019/3/13 001318:55
 * @author:zhang
 */
@Service
public class FinanceAccountServiceImpl implements FinanceAccountService {

    @Autowired
    private FinanceAccountMapper financeAccountMapper;
    @Override
    public FinanceAccount queryFinanceAccountByUid(Integer uid) {

        return financeAccountMapper.selectFinanceAccountByUid(uid);
    }
}
