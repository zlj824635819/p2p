package com.bjpowernode.p2p.service.user;

import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.ResultObject;

/**
 * ClassNmae:UserService
 * Package:com.bjpowernode.p2p.service.user
 * Description:
 *
 * @date:2019/3/11 001117:22
 * @author:zhang
 */

public interface UserService {
    /**
     * 获取平台总人数
     * @return
     */
    Long queryAllUserCount();

    /**
     * 根据手机号码查用户
     * @return
     */
    User queryUserByPhone(String phone);

    /**
     * 用户注册
     * @param phone
     * @param loginPassword
     * @return
     */
    ResultObject register(String phone, String loginPassword);

    /**
     * 根据用户标识更新用户信息
     * @param user
     * @return
     */
    int modifyUserById(User user);

    /**
     * 用户登录
     * @param loginPassword
     * @param phone
     * @return
     */
    User login(String loginPassword, String phone);
}
