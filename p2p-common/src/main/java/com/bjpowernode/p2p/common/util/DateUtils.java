package com.bjpowernode.p2p.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * ClassNmae:DateUtils
 * Package:com.bjpowernode.p2p.common.util
 * Description:
 *
 * @date:2019/3/18 001819:56
 * @author:zhang
 */
public class DateUtils {
    public static Date getDateByAddDays(Date date, Integer count) {

        //创建一个日期处理对象
        Calendar instance = Calendar.getInstance();

        instance.setTime(date);

        instance.add(Calendar.DATE,count);
        return instance.getTime();
    }

    public static Date getDateByAddMonths(Date date, Integer count) {
        Calendar instance = Calendar.getInstance();

        instance.setTime(date);

        instance.add(Calendar.MONTH,count);
        return instance.getTime();
    }

    public static String getTimestamp() {
        return new SimpleDateFormat("yyyymmddHHmmssSSS").format(new Date());
    }
}
