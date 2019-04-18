package com.bjpowernode.p2p.service.user;

import com.bjpowernode.p2p.model.user.FinanceAccount;

/**
 * ClassNmae:FinanceAccountService
 * Package:com.bjpowernode.p2p.service.user
 * Description:
 *
 * @date:2019/3/13 001318:59
 * @author:zhang
 */

public interface FinanceAccountService {
    FinanceAccount queryFinanceAccountByUid(Integer uid);
}
