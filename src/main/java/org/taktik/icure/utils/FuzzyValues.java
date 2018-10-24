/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.utils;

import org.apache.commons.lang3.math.NumberUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

/**
 * This utility class provides methods to detect the type of value submitted to it (dates, ssin,...) and handle the
 * value consequently.<br/>
 * <br/>
 * Detected fully-formed dates are: <br/>
 * <ul>
 * <li>dd/MM/yyyy</li>
 * <li>dd-MM-yyyy</li>
 * <li>yyyyMMdd</li>
 * </ul>
 * Detected partially-formed dates are: <br/>
 * <ul>
 * <li>MM/yyyy</li>
 * <li>MM-yyyy</li>
 * <li>MMyyyy</li>
 * <li>yyyy</li>
 * </ul>
 */
public class FuzzyValues {

	public static int getMaxRangeOf(String text) {

		String fullyFormedDate = toYYYYMMDDString(text);

		String year = fullyFormedDate.substring(0, 4);
		String month = fullyFormedDate.substring(4, 6);
		String day = fullyFormedDate.substring(6, 8);

		StringBuilder sb = new StringBuilder(year);
		if (month.equals("00")) {
			sb.append("99");
		} else {
			sb.append(month);
		}
		if (day.equals("00")) {
			sb.append("99");
		} else {
			sb.append(day);
		}

		return Integer.parseInt(sb.toString());
	}

	public static LocalDateTime getDateTime(long dateTime) {
		long date = dateTime;

		int h = 0;
		int m = 0;
		int s = 0;

		boolean plusOne = false;

		if (dateTime > 99991231l) {
			if (dateTime < 18000101000000L) {
				return Instant.ofEpochMilli(dateTime).atZone(ZoneId.systemDefault()).toLocalDateTime();
			}
			//Full date time format
			long time = dateTime % 1000000l;
			date = dateTime / 1000000l;

			h = (int) (time / 10000l);
			m = (int) ((time / 100l) % 100l);
			s = (int) ((time) % 100l);

			if (h > 24) {
				return null;
			}
			if (m > 60) {
				return null;
			}
			if (s > 60) {
				return null;
			}

			if (s == 60) {
				s = 0;
				m++;
			}

			if (m == 60) {
				m = 0;
				h++;
			}

			if (h == 24) {
				h = 0;
				plusOne = true;
			}
		}

		int y = (int) (date / 10000l);
		int mm = (int) ((date / 100l) % 100l);
		int d = (int) ((date) % 100l);

		if (mm == 0) {
			mm = 1;
		}
		if (d == 0) {
			d = 1;
		}

		return LocalDateTime.of(y, mm, d, h, m, s).plus(Period.ofDays(plusOne ? 1 : 0));
	}

	public static long getCurrentFuzzyDateTime(TemporalUnit precision) {
		return getFuzzyDate(LocalDateTime.now(), precision);
	}

	public static long getCurrentFuzzyDate() {
		return getCurrentFuzzyDateTime(ChronoUnit.DAYS);
	}
	public static long getCurrentFuzzyDateTime() {
		return getCurrentFuzzyDateTime(ChronoUnit.SECONDS);
	}

	public static long getFuzzyDateTime(LocalDateTime dateTime, TemporalUnit precision) {
		int seconds = dateTime.getSecond();
		/*if (seconds == 0 && precision==ChronoUnit.SECONDS) {
			seconds = 60;
			dateTime = dateTime.minusMinutes(1);
		}*/

		int minutes = dateTime.getMinute();
		if (minutes == 0 && precision==ChronoUnit.MINUTES) {
			minutes = 60;
			dateTime = dateTime.minusHours(1);
		}

		int hours = dateTime.getHour();
		if (hours == 0 && precision==ChronoUnit.HOURS) {
			hours = 24;
			dateTime = dateTime.minusDays(1);
		}


		return getFuzzyDate(dateTime, precision) * 1000000l + (precision == ChronoUnit.DAYS ? 0 : (
				hours * 10000l + (precision == ChronoUnit.HOURS ? 0 : (
						minutes * 100l + (precision == ChronoUnit.MINUTES ? 0 : seconds)))));
	}

	public static long getFuzzyDate(LocalDateTime dateTime, TemporalUnit precision) {
		return dateTime.getYear() * 10000l + (precision == ChronoUnit.YEARS ? 0 : (
				dateTime.getMonthValue() * 100l + (precision == ChronoUnit.MONTHS ? 0 : (
						dateTime.getDayOfMonth()
				))));
	}

	/**
	 * Indicates if the submitted text is a fully-formed or partially-formed date.
	 */
	public static boolean isDate(String text) {
		return isPartiallyFormedYYYYMMDD(text) || isPartiallyFormedDashDate(text) || isPartiallyFormedSlashDate(text);
	}

	/**
	 * Indicates if the submitted text is a fully-formed date.
	 */
	public static boolean isFullDate(String text) {
		return isFullyFormedYYYYMMDDDate(text) || isFullyFormedDashDate(text) || isFullyFormedSlashDate(text);
	}

	/**
	 * Indicates if the submitted text has the format of a SSIN. <br/>
	 * It does <b>NOT</b> check if the SSIN is valid!
	 */
	public static boolean isSsin(String text) {
		return NumberUtils.isDigits(text) && text.length() == 11;
	}

	/**
	 * Converts a text value into a YYYYMMDD integer, where DD and MM are replaced by 00 characters if the submitted
	 * value does not contain the information to extract the day or month of the date.<br/>
	 * For example, submitting <i>11/2008</i> will return <i>20081100</i>. <br/>
	 * All dates detected by {@link #isFullDate(String)} and {@link #isDate(String)} will be converted.
	 */
	public static int toYYYYMMDD(String text) {
		return Integer.parseInt(toYYYYMMDDString(text));
	}

	private static String toYYYYMMDDString(String text) {
		String result;

		String[] fields;
		if (isPartiallyFormedDashDate(text)) {
			fields = text.split("-");
		} else if (isPartiallyFormedSlashDate(text)) {
			fields = text.split("/");
		} else {
			fields = new String[]{text};
		}

		String day = "00";
		String month = "00";
		String year;

		if (fields.length == 3) {
			day = fields[0].isEmpty() ? "00" : String.format("%1$02d", Integer.parseInt(fields[0]));
			month = fields[1].isEmpty() ? "00" : String.format("%1$02d", Integer.parseInt(fields[1]));
			year = fields[2].isEmpty() ? "0000" : String.format("%1$04d", Integer.parseInt(fields[2]));

			result = year.concat(month).concat(day);

		} else if (fields.length == 2) {
			month = fields[0].isEmpty() ? "00" : String.format("%1$02d", Integer.parseInt(fields[0]));
			year = fields[1].isEmpty() ? "0000" : String.format("%1$04d", Integer.parseInt(fields[1]));

			result = year.concat(month).concat(day);

		} else {
			if (isPartiallyFormedYYYYMMDD(text)) {
				if (text.length() <= 4) {
					year = String.format("%1$04d", Integer.parseInt(text.substring(0, 4)));
				} else if (text.length() <= 6) {
					month = String.format("%1$02d", Integer.parseInt(text.substring(4)));
					year = String.format("%1$04d", Integer.parseInt(text.substring(0, 4)));
				} else {
					day = String.format("%1$02d", Integer.parseInt(text.substring(6, 8)));
					month = String.format("%1$02d", Integer.parseInt(text.substring(4, 6)));
					year = String.format("%1$04d", Integer.parseInt(text.substring(0, 4)));
				}

				result = year.concat(month).concat(day);

			} else {
				result = text;
			}
		}

		return result;
	}

	private static boolean isFullyFormedDashDate(String text) {
		return text.matches("(0?[1-9]|[12][0-9]|3[01])-(0?[1-9]|1[012])-(\\d{4})");
	}

	private static boolean isFullyFormedSlashDate(String text) {
		return text.matches("(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/(\\d{4})");
	}

	private static boolean isFullyFormedYYYYMMDDDate(String text) {
		return text.matches("(\\d{4})(0?[1-9]|1[012])(0?[1-9]|[12][0-9]|3[01])");
	}

	private static boolean isPartiallyFormedDashDate(String text) {
		return text.matches("(0?[1-9]|[12][0-9]|3[01])?(-)?(0?[1-9]|1[012])-(\\d{4})");
	}

	private static boolean isPartiallyFormedSlashDate(String text) {
		return text.matches("(0?[1-9]|[12][0-9]|3[01])?(/)?(0?[1-9]|1[012])/(\\d{4})");
	}

	private static boolean isPartiallyFormedYYYYMMDD(String text) {
		return NumberUtils.isDigits(text) && text.matches("(\\d{4})(0?[1-9]|1[012])?(0?[1-9]|[12][0-9]|3[01])?");
	}

	public static int compare(long left, long right) {
		return Long.valueOf(left<29991231?left*1000000:left).compareTo(right<29991231?right*1000000:right);
	}
}
