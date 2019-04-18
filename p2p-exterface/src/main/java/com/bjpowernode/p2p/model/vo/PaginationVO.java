package com.bjpowernode.p2p.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * ClassNmae:PaginationVO
 * Package:com.bjpowernode.p2p.model.vo
 * Description:
 *
 * @date:2019/3/12 001218:00
 * @author:zhang
 */
@Data
public class PaginationVO<T> implements Serializable {

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 显示的数据
     */
    private List<T> dataList;
}
