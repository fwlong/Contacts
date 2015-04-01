package com.graduation.contacts.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.util.Log;

import com.graduation.contacts.R;

public class FormatUtils {

	public static String getDateLong2String(Context context, long mills) {
		long now = System.currentTimeMillis();
		long dis = (now - mills) / 1000;
		long mins = dis / 60;
		if (mins == 0) {
			return context.getResources().getString(R.string.just_now);
		} else if (mins > 0 && mins < 60) {
			return context.getResources().getString(R.string.mins_ago, mins);
		} else if (mins / 60 < 10) {
			return context.getResources().getString(R.string.hours_ago,
					mins / 60);
		} else {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date m = new Date(mills);
			Date today = new Date();
			if (today.getYear() != m.getYear())
				return context.getResources().getString(R.string.year,
						m.getYear());
			String date = format.format(m);
			String[] s = date.split("-");
			return context.getResources().getString(R.string.mouth, s[1])
					+ context.getResources().getString(R.string.day, s[2]);

		}
	}

	public static String formatDuration(Context context, long duration) {
		if(duration==0)
		{
			return context.getString(R.string.noanswer);
		}
		long sec = duration ;
		if (sec < 60)
			return context.getResources().getString(R.string.sec, sec);
		if (sec >= 60) {
			long min = sec / 60;
			long se = sec % 60;
			if (min < 60) {
				return context.getResources().getString(R.string.min, min)
						+ (se == 0 ? "" : context.getResources().getString(
								R.string.sec, se));
			} else {
				long hour = min / 60;
				min = min % 60;
				return context.getResources().getString(R.string.hour, hour)
						+ (min == 0 ? "" : context.getResources().getString(
								R.string.min, min));
			}
		}
		return context.getString(R.string.noanswer);

	}

	public static String formatDate(Context context, long mills) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat format1 = new SimpleDateFormat("HH:mm");
		Date m = new Date(mills);
		Date today = new Date();
		if (today.getYear() != m.getYear()) {
			 return context.getString(R.string.year, m.getYear())+"&"+"--:--";
		} else {

			String dd = format.format(m);
			String[] s = dd.split("-");
			 return context.getResources().getString(R.string.mouth, s[1])
					+ context.getResources().getString(R.string.day, s[2])+"&"+
			format1.format(m);
		}

	}
	
	

	public static String getSortString(String str) {
		CharacterParser paraser = CharacterParser.getInstance();
		String pinyin = paraser.getSelling(str);
		String sortString = pinyin.substring(0, 1).toUpperCase();

		if (sortString.matches("[A-Z]")) {
			return sortString;
		} else {
			return "#";
		}
	}

}
