/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package io.github.toolkit.commons.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateTimeUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(DateTimeUtil.class);
	
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String DEFAULT_TIME_MIN = "915120000000"; 
    public static final Date DEFAULT_DATE_MIN = toDate(915120000000L); 
    public static final String DEFAULT_TIME_MAX = "32472115200000";
    public static final Date DEFAULT_DATE_MAX = toDate(32472115200000L); 

    public static java.util.Date nowDate() {
        return new java.util.Date();
    }
    
    public static java.sql.Timestamp nowTimestamp() {
        return getTimestamp(System.currentTimeMillis());
    }
    
    public static java.sql.Timestamp getTimestamp(long time) {
        return new java.sql.Timestamp(time);
    }
    
    public static String format(Date datetime) {
    	return format(datetime, DEFAULT_DATE_TIME_FORMAT);
    }
    
    public static String format(Date datetime, String format) {
    	if (datetime == null) {
			return null;
		}
		return new SimpleDateFormat(format).format(datetime);
    }
    
    public static Date toDate(Long datetime) {
    	return new Date(datetime);
    }
    
    public static Date toDate(String datetimeStr) {
    	return toDate(datetimeStr, DEFAULT_DATE_TIME_FORMAT);
    }
    
    public static Date toDate(String datetimeStr, String format) {
    	SimpleDateFormat sdf = new SimpleDateFormat(format);
    	try {
			return sdf.parse(datetimeStr);
		} catch (ParseException e) {
			logger.warn("Format Error：" + ValidateUtil.isSignedLongMsg);
			return null;
		}
    }
    
    /**
     * Makes a Date from separate Strings for month, day, year, hour, minute, and second.
     *
     * @param monthStr  The month String
     * @param dayStr    The day String
     * @param yearStr   The year String
     * @param hourStr   The hour String
     * @param minuteStr The minute String
     * @param secondStr The second String
     * @return A Date made from separate Strings for month, day, year, hour, minute, and second.
     */
    public static java.util.Date toDate(String monthStr, String dayStr, String yearStr, String hourStr,
            String minuteStr, String secondStr) {
        int month, day, year, hour, minute, second;

        try {
            month = Integer.parseInt(monthStr);
            day = Integer.parseInt(dayStr);
            year = Integer.parseInt(yearStr);
            hour = Integer.parseInt(hourStr);
            minute = Integer.parseInt(minuteStr);
            second = Integer.parseInt(secondStr);
        } catch (Exception e) {
            return null;
        }
        return toDate(month, day, year, hour, minute, second);
    }
    
    /**
     * Makes a Date from separate ints for month, day, year, hour, minute, and second.
     *
     * @param month  The month int
     * @param day    The day int
     * @param year   The year int
     * @param hour   The hour int
     * @param minute The minute int
     * @param second The second int
     * @return A Date made from separate ints for month, day, year, hour, minute, and second.
     */
    public static java.util.Date toDate(int month, int day, int year, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();

        try {
            calendar.set(year, month - 1, day, hour, minute, second);
            calendar.set(Calendar.MILLISECOND, 0);
        } catch (Exception e) {
            return null;
        }
        return new java.util.Date(calendar.getTime().getTime());
    }
    
    /**
     * 计算时间间隔，默认以当前时间结束
     * @param field the calendar field. ex: Calendar.HOUR、Calendar.MINUTE、Calendar.SECOND
     * @param startDate 开始时间
     * @return
     */
    public static Long calcInterval(int field, Date startDate) {
		return calcInterval(field, startDate, DateTimeUtil.nowDate());
	}
    /**
     * 计算时间间隔
     * @param field the calendar field. ex: Calendar.HOUR、Calendar.MINUTE、Calendar.SECOND
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return
     */
    public static Long calcInterval(int field, Date startDate, Date endDate) {
    	long diff = endDate.getTime() - startDate.getTime();
    	Long  result = 0L;
    	if (field == Calendar.SECOND) {
    		result = diff / 1000;
    	} else if (field == Calendar.MINUTE) {
    		result = diff / (1000 * 60);
    	} else if (field == Calendar.HOUR) {
    		result = diff / (1000 * 60 * 60);
    	} else {
    		logger.error("calcInterval 只支持：时、分、秒。");
    		throw new RuntimeException("calcInterval 只支持：时、分、秒。");
    	}
		return result;
	}
    
    
    public static void main(String[] args) {
    	System.out.println(toDate("2016-01-01 00:00:00").getTime());
    	System.out.println(toDate("2016-08-22 00:00:00").getTime());
    }
}
