package service;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class BusinessHourCalculator {
	public enum DayOfWeek {
		SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY;
	}

	public String defaultOpeningTime;
	String defaultClosingTime;

	Map<DayOfWeek, String> openingTimes = new HashMap<DayOfWeek, String>();
	Map<DayOfWeek, String> closingTimes = new HashMap<DayOfWeek, String>();
	Set<String> additionalDaysStoreIsClosed = new HashSet<String>();

	public BusinessHourCalculator(String defaultOpeningTime, String defaultClosingTime) {
		this.defaultOpeningTime = defaultOpeningTime;
		this.defaultClosingTime = defaultClosingTime;

		for (DayOfWeek dayofWeek : DayOfWeek.values()) {
			openingTimes.put(dayofWeek, defaultOpeningTime);
			closingTimes.put(dayofWeek, defaultClosingTime);
		}

	}

	public void setOpeningHours(DayOfWeek dayofWeek, String openingHour, String closingHour) {
		openingTimes.replace(dayofWeek, openingHour);
		closingTimes.replace(dayofWeek, closingHour);
	}

	public void setOpeningHours(String date, String openingHour, String closingHour) {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date day = formatter.parse(date);
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");
			String strOpenDay = simpleDateFormat.format(day).toUpperCase();
			for (DayOfWeek dayofWeek : DayOfWeek.values()) {
				if (strOpenDay.equals(dayofWeek.toString()))

				{
					openingTimes.replace(dayofWeek, openingHour);
					closingTimes.replace(dayofWeek, closingHour);
					break;
				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public void setClosed(DayOfWeek... dayOfWeek) {
		for (DayOfWeek dayOfWeek2 : dayOfWeek) {
			openingTimes.remove(dayOfWeek2);
			closingTimes.remove(dayOfWeek2);
		}

	}

	public void setClosed(String... days) {
		for (String day : days) {
			additionalDaysStoreIsClosed.add(day);
		}

	}

	public Date calculateDeadlinel(long timeInterval, String startDateTime) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		Date sDateTime = formatter.parse(startDateTime);
		sDateTime = getValidBusinessDay(sDateTime);
		Date openingHour = getOpeningHour(sDateTime);
		if (sDateTime.before(openingHour)) {
			sDateTime = openingHour;

		}
		Date closingHour = getClosingHour(sDateTime);
		if (sDateTime.after(closingHour)) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(sDateTime);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			Date nextDate = getValidBusinessDay(calendar.getTime());
			sDateTime = getOpeningHour(nextDate);
			closingHour = getClosingHour(nextDate);
		}
		long diffInSec = TimeUnit.MILLISECONDS.toSeconds(closingHour.getTime() - sDateTime.getTime());
		diffInSec = diffInSec < 0 ? -diffInSec : diffInSec;
		if (timeInterval > diffInSec) {
			long remainingTime = diffInSec - timeInterval;
			remainingTime = remainingTime < 0 ? -remainingTime : remainingTime;
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(sDateTime);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			Date nextWrokingDay = getValidBusinessDay(calendar.getTime());
			openingHour = getOpeningHour(nextWrokingDay);
			closingHour = getClosingHour(nextWrokingDay);
			while (true) {
				diffInSec = 0;
				diffInSec = TimeUnit.MILLISECONDS.toSeconds(closingHour.getTime() - openingHour.getTime());
				if (remainingTime > diffInSec) {
					remainingTime = remainingTime - diffInSec;
					remainingTime = remainingTime < 0 ? -remainingTime : remainingTime;
					calendar.add(Calendar.DAY_OF_MONTH, 1);
					nextWrokingDay = getValidBusinessDay(calendar.getTime());
					openingHour = getOpeningHour(nextWrokingDay);
					closingHour = getClosingHour(nextWrokingDay);
				} else {
					timeInterval = remainingTime;
					sDateTime = openingHour;
					break;

				}

			}

		}

		Calendar calender = Calendar.getInstance();
		calender.setTime(sDateTime);
		calender.add(Calendar.SECOND, (int) timeInterval);

		return calender.getTime();
	}

	public Date getValidBusinessDay(Date sDateTime) {

		Calendar calender = Calendar.getInstance();
		calender.setTime(sDateTime);
		while (iclosed(calender.getTime())) {
			calender.add(Calendar.DAY_OF_MONTH, 1);

		}

		return calender.getTime();
	}

	public boolean iclosed(Date sDateTime) {
		Calendar calender = Calendar.getInstance();
		calender.setTime(sDateTime);
		DayOfWeek dayOfWeek = DayOfWeek.values()[calender.get(Calendar.DAY_OF_WEEK) - 1];
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String startDate = formatter.format(sDateTime);

		if (additionalDaysStoreIsClosed.contains(startDate) || !openingTimes.containsKey(dayOfWeek)) {
			return true;
		}
		return false;
	}

	public Date getOpeningHour(Date sDateTime) throws ParseException {
		Calendar sDateTimeCalender = Calendar.getInstance();
		sDateTimeCalender.setTime(sDateTime);
		DayOfWeek dayOfWeek = DayOfWeek.values()[sDateTimeCalender.get(Calendar.DAY_OF_WEEK) - 1];

		String openingHour = openingTimes.get(dayOfWeek);
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
		Date openingHourDate = formatter.parse(openingHour);
		Calendar openingTimeCalendar = Calendar.getInstance();
		openingTimeCalendar.setTime(openingHourDate);
		sDateTimeCalender.set(Calendar.HOUR_OF_DAY, openingTimeCalendar.get(Calendar.HOUR_OF_DAY));
		sDateTimeCalender.set(Calendar.MINUTE, openingTimeCalendar.get(Calendar.MINUTE));
		return sDateTimeCalender.getTime();

	}

	public Date getClosingHour(Date sDateTime) throws ParseException {
		Calendar sDateTimeCalender = Calendar.getInstance();
		sDateTimeCalender.setTime(sDateTime);
		DayOfWeek dayOfWeek = DayOfWeek.values()[sDateTimeCalender.get(Calendar.DAY_OF_WEEK) - 1];
		String closingHour = closingTimes.get(dayOfWeek);
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
		Date closingHourDate = formatter.parse(closingHour);
		Calendar closingTimeCalendar = Calendar.getInstance();
		closingTimeCalendar.setTime(closingHourDate);
		sDateTimeCalender.set(Calendar.HOUR_OF_DAY, closingTimeCalendar.get(Calendar.HOUR_OF_DAY));
		sDateTimeCalender.set(Calendar.MINUTE, closingTimeCalendar.get(Calendar.MINUTE));

		return sDateTimeCalender.getTime();
	}

}
