package com.bjpowernode.p2p.service.user;

import com.bjpowernode.p2p.common.constants.Constants;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.mapper.user.UserMapper;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.ResultObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * ClassNmae:UserServiceImpl
 * Package:com.bjpowernode.p2p.service.user
 * Description:
 *
 * @date:2019/3/11 001117:24
 * @author:zhang
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    @Override
    public Long queryAllUserCount() {
        //绑定指定key对应的value对象
        BoundValueOperations<String, Object> boundValueOperations = redisTemplate.boundValueOps(Constants.ALL_USER_COUNT);
        //从ridis取得平台注册总人数
        Long allUserCount = (Long) boundValueOperations.get();
        if (null == allUserCount) {

            //去数据库查询
            allUserCount = userMapper.selectAllUserCount();
            //将该值存入redis数据库
            boundValueOperations.set(allUserCount,15, TimeUnit.MINUTES);
        }
        return allUserCount;
    }

    @Override
    public User queryUserByPhone(String phone) {
        return userMapper.selectUserByPhone(phone);
    }

    @Override
    public ResultObject register(String phone, String loginPassword) {
        ResultObject resultObject =new ResultObject();
        resultObject.setErrorCode(Constants.SUCCESS);
        //新增用户
        User user = new User();
        user.setPhone(phone);
        user.setLoginPassword(loginPassword);
        user.setAddTime(new Date());
        user.setLastLoginTime(new Date());
        int insertUserCount = userMapper.insertSelective(user);
        if (insertUserCount>0){
            User userInfo = userMapper.selectUserByPhone(phone);
            FinanceAccount financeAccount = new FinanceAccount();
            financeAccount.setAvailableMoney(888.0);
            financeAccount.setUid(userInfo.getId());
            int insertFinanceAccountCount = financeAccountMapper.insertSelective(financeAccount);
            if (insertFinanceAccountCount<=0){
                resultObject.setErrorCode(Constants.FAIL);
            }else {
                resultObject.setErrorCode(Constants.SUCCESS);
            }
        }
        //新增账户
        return resultObject;
    }

    @Override
    public int modifyUserById(User user) {


        return userMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    public User login(String loginPassword, String phone) {
        //根据手机号码和登录密码查询用户信息
        User user = userMapper.selectUserByPhoneAndLoginPassword(loginPassword,phone);

        if (null != user) {
        //更新最近登录时间
            User updataUser = new User();
            updataUser.setId(user.getId());
            updataUser.setLastLoginTime(new Date());
            userMapper.updateByPrimaryKeySelective(updataUser);

        }
        return user;
    }
}
