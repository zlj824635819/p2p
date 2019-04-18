package com.bjpowernode.p2p.service.loan;

/**
 * ClassNmae:RedisService
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 *
 * @date:2019/3/15 001521:08
 * @author:zhang
 */
public interface RedisService {

    /**
     * 将指定的value放到redis中
     * @param key
     * @param value
     */
    void put(String key, String value);

    /**
     * 获取redis中唯一数字
     * @return
     */
    Long getOnlyNumber();
}
