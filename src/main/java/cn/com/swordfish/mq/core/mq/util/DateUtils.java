package cn.com.swordfish.mq.core.mq.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
	public static String DATE_FORMAT_STRING_COMMON = "yyyy-MM-dd";
	public static String DATE_FORMAT_STRING_DEFAULT = "yyyy-MM-dd HH:mm:ss";
	public static String DATE_FORMAT_STRING_HM = "yyyy-MM-dd HH:mm";
	public static String DATE_FORMAT_STRING_JOIN = "yyyyMMddHHmmss";
	public static String DATE_FORMAT_STRING_TIME = "HH:mm:ss";
	public static String DATE_FORMAT_STRING_DEFAULT_CN = "yyyy年MM月dd日 HH时mm分ss秒";
	public static String DATE_FORMAT_STRING_COMMON_CN = "yyyy年MM月dd日";

	/**
	 * 取当前年
	 * 
	 * @return
	 */
	public static String getCurrentYear() {
		Calendar now = Calendar.getInstance();
		return new Integer(now.get(Calendar.YEAR)).toString();
	}

	/**
	 * 取当前月
	 * 
	 * @return
	 */
	public static String getCurrentMonth() {
		Calendar now = Calendar.getInstance();
		return new Integer(now.get(Calendar.MONTH) + 1).toString();
	}

	/**
	 * 取当前天的字符串
	 * 
	 * @return
	 */
	public static String getCurrentDay() {
		SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_STRING_COMMON);
		return format.format(new Date());
	}

	/**
	 * 根据某个Date得到字符串，格式为yyyy-MM-dd
	 * 
	 * @param date
	 * @return @
	 */
	public static String getString(Date date) {
		if (date == null)
			return "";
		SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_STRING_COMMON);
		return format.format(date);
	}

	/**
	 * 根据某个Date得到格式化后的字符串
	 * 
	 * @param date
	 * @param formatString
	 * @return
	 */
	public static String getString(Date date, String formatString) {
		if (formatString == null)
			return getString(date);
		SimpleDateFormat format = new SimpleDateFormat(formatString);
		return format.format(date);
	}

	/**
	 * 得到某个Date的日期时间字符串，格式为yyyy-MM-dd HH:mm:ss
	 * 
	 * @param date
	 * @return
	 */
	public static String getTimeString(Date date) {
		SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_STRING_DEFAULT);
		return format.format(date);
	}

	/**
	 * 得到某个data的日期时间字符串，格式为yyyy-MM-dd HH:mm
	 * 
	 * @param date
	 * @return
	 */
	public static String getTimeStringHM(Date date) {
		SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_STRING_HM);
		return format.format(date);
	}

	/**
	 * 得到某个日期的时间部分，格式为HH:MM:SS
	 * 
	 * @param date
	 * @return
	 */
	public static String getTimeStringOnly(Date date) {
		SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_STRING_TIME);
		return format.format(date);
	}

	/**
	 * 根据字符串得到某个日期，格式为yyyy-MM-dd
	 * 
	 * @param dateString
	 * @return
	 */
	public static Date getDate(String dateString) {
		SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_STRING_COMMON);
		if (dateString != null && !"".equals(dateString)) {
			try {
				return format.parse(dateString);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 根据字符串和格式得到某个日期
	 * 
	 * @param dateString
	 * @param pattern
	 * @return
	 */
	public static Date getDate(String dateString, String pattern) {
		if (dateString == null || "".equals(dateString))
			return null;
		if (pattern == null || "".equals(pattern))
			return getDate(dateString);
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		try {
			return format.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据字符串得到某个日期，格式为yyyy-MM-dd HH:mm:ss
	 * 
	 * @param dateString
	 * @return
	 */
	public static Date getDateFromTimeString(String dateString) {
		SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_STRING_DEFAULT);
		try {
			return format.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据字符串得到某个日期，格式为YYYY-MM-DD HH:MM
	 * 
	 * @param dateString
	 * @return
	 */
	public static Date getDateFromTimeStringHM(String dateString) {
		dateString += ":00";
		SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_STRING_DEFAULT);
		try {
			return format.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 取某个日期的年份
	 * 
	 * @param date
	 * @return
	 */
	public static String getYear(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return String.valueOf(calendar.get(Calendar.YEAR));
	}

	/**
	 * 取某个日期的月份
	 * 
	 * @param date
	 * @return
	 */
	public static String getMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return String.valueOf(calendar.get(Calendar.MONTH) + 1);
	}

	/**
	 * 取某个日期的天份
	 * 
	 * @param date
	 * @return
	 */
	public static String getDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return String.valueOf(calendar.get(Calendar.DATE));
	}

	/**
	 * 取某一年的第一天
	 * 
	 * @param year
	 * @return
	 */
	public static Date getFirstDayOfYear(String year) {
		return getDate(year + "-1-1");
	}

	/**
	 * 取某一年的最后一天
	 * 
	 * @param year
	 * @return
	 */
	public static Date getLastDayOfYear(String year) {
		return getDate(year + "-12-31");
	}

	/**
	 * 取某年某月的第一天
	 * 
	 * @param month
	 * @return
	 */
	public static Date getFirstDayOfMonth(String year, String month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Integer.parseInt(year), Integer.parseInt(month) - 1, 1);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		return calendar.getTime();
	}

	/**
	 * 取某年某月的最后一天
	 * 
	 * @param month
	 * @return
	 */
	public static Date getLastDayOfMonth(String year, String month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Integer.parseInt(year), Integer.parseInt(month) - 1, 1);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return calendar.getTime();
	}

	/**
	 * 取某年某月的第一天
	 * 
	 * @param month
	 * @return
	 */
	public static String getFirstDayOfMonth(Date date) {
		try {
			Calendar calendar = getCalendarFromDate(date);
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH) + 1;
			String result = year + "-" + month + "-1";
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 取某年某月的最后一天
	 * 
	 * @param month
	 * @return
	 */
	public static String getLastDayOfMonth(Date date) {
		try {
			Calendar calendar = getCalendarFromDate(date);
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH) + 1;
			int lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			String result = year + "-" + month + "-" + lastDayOfMonth;
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 取得某天对应的本周第一天
	 * 
	 * @param month
	 * @return
	 */
	public static String getFirstDateOfWeek(Calendar calendar) {
		Calendar result = (Calendar) calendar.clone();
		result.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return getStringFromCalendar(result);
	}

	/**
	 * 取得某天对应的本周第一天
	 * 
	 * @param month
	 * @return
	 */
	public static Calendar getFirstDateOfWeekOfCalendar(Calendar calendar) {
		Calendar result = (Calendar) calendar.clone();
		result.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return result;
	}

	/**
	 * 取得某天对应的本周最后一天
	 * 
	 * @param month
	 * @return
	 */
	static public String getFinalDateOfWeek(Calendar calendar) {
		Calendar result = (Calendar) calendar.clone();
		result.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		result.add(Calendar.DATE, 8 - result.get(Calendar.DAY_OF_WEEK));
		return getStringFromCalendar(result);

	}

	/**
	 * 取得某天对应的本周最后一天
	 * 
	 * @param month
	 * @return
	 */
	static public Calendar getFinalDateOfWeekOfCalendar(Calendar calendar) {
		Calendar result = (Calendar) calendar.clone();
		result.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		result.add(Calendar.DATE, 8 - result.get(Calendar.DAY_OF_WEEK));
		return result;
	}

	/**
	 * 取得某天对应的本周第一天
	 * 
	 * @param month
	 * @return
	 */
	public static String getFirstDateOfWeek(Date date) {
		return getFirstDateOfWeek(getCalendarFromDate(date));
	}

	/**
	 * 将Date变成日期串（20050430000000）：24小时制
	 * 
	 * @param date
	 * @return
	 */
	public static String getDatetimeStringNumber(Date date) {
		DateFormat df = new SimpleDateFormat(DATE_FORMAT_STRING_JOIN, Locale.US);
		String dateStr = df.format(date);
		return dateStr;
	}

	/**
	 * 取得某天对应的本周最后一天
	 * 
	 * @param month
	 * @return
	 */
	static public String getFinalDateOfWeek(Date date) {
		return getFinalDateOfWeek(getCalendarFromDate(date));
	}

	/**
	 * 根据年和第几周 取得周的第一天
	 * 
	 * @param month
	 * @return
	 */
	public static String getFirstDateOfWeek(int year, int week) {
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.WEEK_OF_YEAR, week);
		return getFirstDateOfWeek(calendar);
	}

	/**
	 * 根据年和第几周 取得周的最后一天
	 * 
	 * @param month
	 * @return
	 */
	public static String getFinalDateOfWeek(int year, int week) {
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.WEEK_OF_YEAR, week);
		return getFinalDateOfWeek(calendar);
	}

	/**
	 * 根据日历得到日期字符串
	 * 
	 * @param calendar
	 * @return
	 */
	public static String getStringFromCalendar(Calendar calendar) {
		int currentYear = calendar.get(Calendar.YEAR);
		int currentMonth = calendar.get(Calendar.MONTH) + 1;
		int currentDay = calendar.get(Calendar.DATE);
		return String.valueOf(currentYear) + "-" + String.valueOf(currentMonth) + "-" + String.valueOf(currentDay);
	}

	/**
	 * 根据日历得到给定格式的字符串
	 * 
	 * @param calendar
	 * @param formatString
	 * @return
	 */
	public static String getStringFromCalendar(Calendar calendar, String formatString) {
		if (formatString == null)
			return getString(calendar.getTime());
		SimpleDateFormat format = new SimpleDateFormat(formatString);
		return format.format(calendar.getTime());
	}

	/**
	 * 根据Date 得到对应的Calendar
	 * 
	 * @param date
	 * @return @
	 */
	public static Calendar getCalendarFromDate(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c;
	}

	/**
	 * 判断两个日期是否是同一天
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isSameDay(Date date1, Date date2) {
		return org.apache.commons.lang.time.DateUtils.isSameDay(date1, date2);
	}

	/**
	 * 得到下一年
	 * 
	 * @param year
	 * @return
	 */
	public static String getNextYear(String year) {
		if (year == null || "".equals(year))
			return "";
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, Integer.parseInt(year));
		c.add(Calendar.YEAR, 1);
		int nextYear = c.get(Calendar.YEAR);
		return String.valueOf(nextYear);
	}

	/**
	 * 得到上一年
	 * 
	 * @param year
	 * @return
	 */
	public static String getPrevYear(String year) {
		if (year == null || "".equals(year))
			return "";
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, Integer.parseInt(year));
		c.add(Calendar.YEAR, -1);
		int nextYear = c.get(Calendar.YEAR);
		return String.valueOf(nextYear);
	}

	/**
	 * 取前或者后多少年
	 * 
	 * @param year
	 * @param num
	 * @return
	 */
	public static String getManyBackYear(String year, Integer num) {
		if (year == null || "".equals(year))
			return "";
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, Integer.parseInt(year));
		c.add(Calendar.YEAR, num);
		int nextYear = c.get(Calendar.YEAR);
		return String.valueOf(nextYear);
	}

	/**
	 * 得到某一天的开始时间：即yyyy-mm-dd 00:00:00 000
	 * 
	 * @param date
	 * @return
	 */
	public static Date getStartOfDay(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.AM_PM, Calendar.AM);
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 000);
		return c.getTime();
	}

	/**
	 * 得到某一天的结束时间：即yyyy-mm-dd 11:59:59 999
	 * 
	 * @param date
	 * @return
	 */
	public static Date getEndOfDay(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.AM_PM, Calendar.PM);
		c.set(Calendar.HOUR, 11);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		return c.getTime();
	}

	/**
	 * 得到某天的下一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getNextDay(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, 1);
		return c.getTime();
	}

	/**
	 * 取当前起某段时间间隔后的时间
	 * 
	 * @param interval
	 * @return
	 */
	public static final Calendar getCalendarAfterInterval(long interval) {
		Calendar now = Calendar.getInstance();
		long time = now.getTimeInMillis() + interval;
		now.setTimeInMillis(time);
		return now;
	}

	/**
	 * 得到某天是周几
	 * 
	 * @param date
	 * @return
	 */
	public static String getWeekString(Date date) {
		Calendar c = getCalendarFromDate(date);
		int i = c.get(Calendar.DAY_OF_WEEK);
		i = i - 1 == 0 ? 7 : i - 1;
		return getWeekString(i);
	}

	/**
	 * 根据数字得到该数字对应的是星期几，从1开始
	 * 
	 * @param i
	 * @return
	 */
	public static String getWeekString(int i) {
		String result = "";
		switch (i) {
		case 1: {
			result = "一";
			break;
		}
		case 2: {
			result = "二";
			break;
		}
		case 3: {
			result = "三";
			break;
		}
		case 4: {
			result = "四";
			break;
		}
		case 5: {
			result = "五";
			break;
		}
		case 6: {
			result = "六";
			break;
		}
		case 7: {
			result = "日";
			break;
		}
		default:
			break;
		}
		return result;
	}

	/**
	 * 两天的间隔数（开始时间减去结束时间，若开始时间早于结束时间，则返回负值）
	 * 
	 * @author tantao
	 * @param begin
	 *            Date 开始日期
	 * @param end
	 *            Date 结束日期
	 * 
	 * @return 两天的间隔数
	 */
	public static Long getIntervalDate(Date begin, Date end) {
		return getIntervalHours(begin, end) / 24;
	}

	/**
	 * 两个日期的间隔小时数（开始时间减去结束时间，若开始时间早于结束时间，则返回负值）
	 * 
	 * @param begin
	 *            Date 开始日期
	 * @param end
	 *            Date 结束日期
	 * 
	 * @return 两个日期的间隔小时数
	 */
	public static Long getIntervalHours(Date begin, Date end) {
		return getIntervalMinutes(begin, end) / 60;
	}

	/**
	 * 两个日期的间隔分钟数（开始时间减去结束时间，若开始时间早于结束时间，则返回负值）
	 * 
	 * @param begin
	 *            Date 开始日期
	 * @param end
	 *            Date 结束日期
	 * 
	 * @return 两个日期的间隔分钟数
	 */
	public static Long getIntervalMinutes(Date begin, Date end) {
		if (begin == null || end == null)
			return 0L;
		Long num = begin.getTime() - end.getTime();
		num = num / 1000 / 60;
		return num;
	}

	/**
	 * 得到当前年的汉字写法
	 * 
	 * @return
	 */
	public static String getChineseYear() {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		String[] str = new String[] { "〇", "一", "二", "三", "四", "五", "六", "七", "八", "九" };
		String yearStr = String.valueOf(year);
		char[] ch = yearStr.toCharArray();
		StringBuilder sb = new StringBuilder();
		for (char c : ch) {
			Integer in = Integer.parseInt(String.valueOf(c));
			sb.append(str[in]);
		}
		return sb.append("年").toString();
	}

	/**
	 * 判断startDateStr所代表的日期是否在endDateStr之前
	 * 
	 * @param startDateStr
	 *            开始日期字符串
	 * @param endDateStr
	 *            结束日期字符串
	 * @return 若两字符串相等，或startDateStr在endDateStr之前，则返回true；否则返回false
	 */
	public static boolean isDateBefore(String startDateStr, String endDateStr) {
		if (startDateStr.equals(endDateStr)) {
			return true;
		}
		DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_STRING_COMMON);
		try {
			Date startDate = dateFormat.parse(startDateStr);
			Date endDate = dateFormat.parse(endDateStr);
			return startDate.before(endDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 将日期增加指定年数
	 * 
	 * @param date
	 *            日期
	 * @param years
	 *            增加的年数,可以为正负数
	 * @return
	 */
	public static Date addYears(Date date, int years) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.YEAR, years);
		return c.getTime();
	}

	/**
	 * 将日期增加指定月数
	 * 
	 * @param date
	 *            日期
	 * @param months
	 *            增加的月数,可以为正负数
	 * @return
	 */
	public static Date addMonths(Date date, int months) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MONTH, months);
		return c.getTime();
	}

	/**
	 * 将日期增加指定周数
	 * 
	 * @param date
	 *            日期
	 * @param weeks
	 *            增加的天数,可以为正负数
	 * @return
	 */
	public static Date addWeeks(Date date, int weeks) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.WEEK_OF_YEAR, weeks);
		return c.getTime();
	}

	/**
	 * 将日期增加指定天数
	 * 
	 * @param date
	 *            日期
	 * @param days
	 *            增加的天数,可以为正负数
	 * @return
	 */
	public static Date addDays(Date date, int days) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, days);
		return c.getTime();
	}

	/**
	 * 将日期增加指定小时数
	 * 
	 * @param date
	 *            日期
	 * @param hours
	 *            增加的小时数,可以为正负数
	 * @return
	 */
	public static Date addHours(Date date, int hours) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.HOUR_OF_DAY, hours);
		return c.getTime();
	}

	/**
	 * 将日期增加指定分钟数
	 * 
	 * @param date
	 *            日期
	 * @param minutes
	 *            增加的分钟数,可以为正负数
	 * @return
	 */
	public static Date addMinutes(Date date, int minutes) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MINUTE, minutes);
		return c.getTime();
	}

	/**
	 * 将日期增加指定秒数
	 * 
	 * @param date
	 *            日期
	 * @param seconds
	 *            增加的秒数,可以为正负数
	 * @return
	 */
	public static Date addSeconds(Date date, int seconds) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.SECOND, seconds);
		return c.getTime();
	}

	// 测试方法
	public static void main(String[] args) {
		Date d = new Date();
		// @author Administrator
		System.out.println("当前年：" + getCurrentYear());
		System.out.println("当前月：" + getCurrentMonth());
		System.out.println("当前天：" + getCurrentDay());
		System.out.println(getString(d));
		System.out.println("d is null:" + getString(null));
		System.out.println("!!!!!!!!!!!!!!!!!!!");
		System.out.println("fmt is null:" + getString(d, null));
		System.out.println("d is null and fmt is null:" + getString(null, null));
		System.out.println("!!!!!!!!!!!!!!!!!!!");
		System.out.println(getTimeString(d));
		System.out.println(getTimeStringOnly(d));
		System.out.println(getDate("2008-2-25"));
		System.out.println(getDateFromTimeString("2009-12-25 12:25:30"));
		System.out.println(getFirstDayOfMonth("2009", "1"));
		System.out.println(getLastDayOfMonth("2009", "2"));
		System.out.println("日期年：" + getYear(d));
		System.out.println("日期月：" + getMonth(d));
		System.out.println("日期天：" + getDay(d));
		System.out.println("start:" + getStartOfDay(d));
		System.out.println("end:" + getEndOfDay(d));
		System.out.println("两天的间隔数+：" + getIntervalDate(new Date(), new Date(0)));
		System.out
				.println("两天的间隔数-：" + getIntervalDate(new Date(), new Date(System.currentTimeMillis() + 10000000000L)));
		try {
			System.out.println("月：：：：： " + getLastDayOfMonth(d));
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("5 years ago:" + addYears(new Date(), 5));
		System.out.println("5 months ago:" + addMonths(new Date(), 5));
		System.out.println("5 weeks ago:" + addWeeks(new Date(), 5));
		System.out.println("5 days ago:" + addDays(new Date(), 5));
		System.out.println("5 hours ago:" + addHours(new Date(), 5));
		System.out.println("5 minutes ago:" + addMinutes(new Date(), 5));
		System.out.println("5 seconds ago:" + addSeconds(new Date(), 5));
	}
}
