package com.renyuzhuo.chat.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期格式化工具类
 */
public class DateUtils {
    /**
     * 将日期显示为人类易读的字符串
     *
     * @param date 时间
     * @return 人类可读的字符串
     */
    public static final String dateToRead(String date) {
        try {
            date = parseDateWithT(date);
            String readStr = "";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String now = sdf.format(new Date());
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            String yesterday = sdf.format(cal.getTime());

            int[] timeShow = splitDate(date);
            int[] timeNow = splitDate(now);
            int[] timeYesterday = splitDate(yesterday);

            if (timeShow != null && timeNow != null && timeYesterday != null) {
                if (timeShow[0] == timeNow[0] && timeShow[1] == timeNow[1] && timeShow[2] == timeNow[2]) {
                    readStr = date.substring(11, 16);
                    // 今天
                } else if (timeShow[0] == timeYesterday[0] && timeShow[1] == timeYesterday[1] && timeShow[2] == timeYesterday[2]) {
                    readStr = "昨天 " + date.substring(11, 16);
                    // 昨天
                } else if (timeShow[0] == timeNow[0]) {
                    readStr = date.substring(5, 16);
                    // 今年
                } else {
                    // 不是今年
                    readStr = date.substring(0, 16);
                }

                return readStr;

            } else {
                return date;
            }
        } catch (Exception e) {
            return date;
        }

    }

    /**
     * 格式化时间是否包含'T'，如果是格式化时间为"yyyy-MM-dd HH:mm:ss"格式
     *
     * @param date 时间字符串
     * @return 格式化后字符串
     */
    public static String parseDateWithT(String date) {
        if (date.contains("T")) {
            date = date.replace("T", " ");
            date = date.substring(0, 19);
        }
        return date;
    }

    /**
     * 将时间切割为年月日时分秒，放到dateSplit数组中
     *
     * @param date 要切割的字符串,格式为:2012-12-12 12:12:12
     */
    private static int[] splitDate(String date) {
        try {
            int[] dateSplit = new int[6];
            String[] temp = date.split(" ");
            String[] month = null;
            String[] time;
            if (temp.length >= 1) {
                month = temp[0].split("-");
            }
            if (temp.length >= 2) {
                time = temp[1].split(":");
                dateSplit[0] = Integer.valueOf(month[0]);
                dateSplit[1] = Integer.valueOf(month[1]);
                dateSplit[2] = Integer.valueOf(month[2]);
                dateSplit[3] = Integer.valueOf(time[0]);
                dateSplit[4] = Integer.valueOf(time[1]);
                dateSplit[5] = Integer.valueOf(time[2]);

                return dateSplit;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断两个时间字符串间隔是够超过五分钟
     *
     * @param date1 第一个时间
     * @param date2 第二个时间
     * @return 是否间隔超过五分钟
     */
    public static boolean getDateSpace(String date1, String date2) {
        Calendar c = Calendar.getInstance();
        try {
            date1 = parseDateWithT(date1);
            date2 = parseDateWithT(date2);

            c.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date1));
            long now = c.getTimeInMillis();
            c.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date2));
            long last = c.getTimeInMillis();

            if (now - last > 5 * 60 * 1000) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }

}
