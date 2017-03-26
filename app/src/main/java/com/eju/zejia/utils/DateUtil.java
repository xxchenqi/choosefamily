package com.eju.zejia.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    /*
     * 按照固定格式转换时间
     * @param date
     * @param oldPattern yyyy-MM-dd HH:mm:ss
     * @param newPattern yyyy年MM月dd HH:mm:ss
     * @return
     */
    public static String StringPattern(String date, String oldPattern,
                                       String newPattern) {
        if (date == null || oldPattern == null || newPattern == null)
            return "";
        SimpleDateFormat sdf1 = new SimpleDateFormat(oldPattern, Locale.getDefault()); // 实例化模板对象
        SimpleDateFormat sdf2 = new SimpleDateFormat(newPattern, Locale.getDefault()); // 实例化模板对象
        Date d = null;
        try {
            d = sdf1.parse(date); // 将给定的字符串中的日期提取出来
        } catch (Exception e) { // 如果提供的字符串格式有错误，则进行异常处理
            e.printStackTrace(); // 打印异常信息
        }
        return sdf2.format(d);
    }

    /*
     * 得到当前时间格式自己填写
     *
     * @param fromat 格式
     * @return
     */
    public static String getCurrentDate(String fromat) {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat(fromat, Locale.getDefault());
        return dateFormat.format(date);
    }

    /**
     * 比较日期大小
     *
     * @param s1 sys_time
     * @param s2 end_date
     * @return result 等于0：c1=c2  小于0：c1 < c2  大与0：c1 > c2
     */
    public static int compareDate(String s1, String s2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        try {
            c1.setTime(df.parse(s1));
            c2.setTime(df.parse(s2));
        } catch (ParseException e) {
            e.printStackTrace();
            System.err.println("---时间格式不正确");
        }
//        int result = c1.compareTo(c2);
//        if (result == 0)
//            System.out.println("---c1相等c2");
//        else if (result < 0)
//            System.out.println("---c1小于c2");
//        else
//            System.out.println("---c1大于c2");
        return c1.compareTo(c2);
    }

    public static String second2Date(String time) {
        Date today = new Date(Long.valueOf(time + "000"));
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return f.format(today);
    }

}
