package com.JTechMod.utile;

import java.sql.Date;
import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.content.Context;

/**
 * 通用方法类
 * 
 * @author wuxubaiyang
 *
 */
public class Utils {
	/**
	 * 计算已经过的时间(时间戳单位为秒，当前系统时间需要除以1000)
	 * 
	 * @param oldTime
	 *            原始时间
	 * @return 经过时间
	 */
	public static String getPassTime(long oldTime) {
		return getPassTime(oldTime, System.currentTimeMillis() / 1000);
	}

	/**
	 * 计算已经过的时间
	 * 
	 * @param oldTime
	 *            原始时间
	 * @param nowTime
	 *            当前时间
	 * @return 经过时间
	 */
	public static String getPassTime(long oldTime, long nowTime) {
		String temp = "";
		try {
			long diff = nowTime - oldTime;
			long days = diff / (60 * 60 * 24);
			long hours = (diff - days * (60 * 60 * 24)) / (60 * 60);
			long minutes = (diff - days * (60 * 60 * 24) - hours * (60 * 60)) / 60;
			if (days > 0) {
				temp = days + "天前";
			} else if (hours > 0) {
				temp = hours + "小时前";
			} else {
				temp = minutes + "分钟前";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return temp;
	}

	/**
	 * 格式化日期(yy-MM-dd hh:mm)
	 * 
	 * @return
	 */
	public static String getDate(long timeTemp) {
		return getDate(timeTemp, "yyyy-MM-dd hh:mm");
	}

	/**
	 * 秒为单位的时间戳的格式化
	 * 
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getDate(long timeTemp, String pattern) {
		Date date = new Date(timeTemp);
		return new SimpleDateFormat(pattern).format(date);
	}

	/**
	 * dip转换为px
	 * 
	 * @return px单位的数值
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * px转换为dip
	 * 
	 * @return dip单位的数值
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}