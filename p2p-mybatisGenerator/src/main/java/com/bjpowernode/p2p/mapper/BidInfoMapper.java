package com.bjpowernode.p2p.mapper;

import com.bjpowernode.p2p.model.BidInfo;

public interface BidInfoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_bid_info
     *
     * @mbggenerated Sat Mar 09 17:46:50 CST 2019
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_bid_info
     *
     * @mbggenerated Sat Mar 09 17:46:50 CST 2019
     */
    int insert(BidInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_bid_info
     *
     * @mbggenerated Sat Mar 09 17:46:50 CST 2019
     */
    int insertSelective(BidInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_bid_info
     *
     * @mbggenerated Sat Mar 09 17:46:50 CST 2019
     */
    BidInfo selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_bid_info
     *
     * @mbggenerated Sat Mar 09 17:46:50 CST 2019
     */
    int updateByPrimaryKeySelective(BidInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_bid_info
     *
     * @mbggenerated Sat Mar 09 17:46:50 CST 2019
     */
    int updateByPrimaryKey(BidInfo record);
}