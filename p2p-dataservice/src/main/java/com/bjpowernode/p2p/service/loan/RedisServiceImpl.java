package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.common.constants.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * ClassNmae:RedisServiceImpl
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 *
 * @date:2019/3/15 001521:10
 * @author:zhang
 */
@Service
public class RedisServiceImpl implements RedisService {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;


    @Override
    public void put(String key, String value) {
        redisTemplate.opsForValue().set(key,value,60, TimeUnit.SECONDS);
    }

    @Override
    public Long getOnlyNumber() {

        return redisTemplate.opsForValue().increment(Constants.ONLY_NUMBER,1L);
    }

}
