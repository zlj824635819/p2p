package com.bjpowernode.p2p.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassNmae:BidUserTop
 * Package:com.bjpowernode.p2p.model.vo
 * Description:
 *
 * @date:2019/3/18 001816:13
 * @author:zhang
 */
@Data
public class BidUserTop implements Serializable {

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 分数
     */
    private Double score;
}
