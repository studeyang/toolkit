package io.github.toolkit.commons.format;

import io.github.toolkit.commons.utils.DateTimeUtil;
import org.springframework.format.Formatter;
import org.springframework.util.NumberUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

public class TimestampFormatter implements Formatter<java.util.Date> {
	
	@Override
	public String print(Date date, Locale locale) {
		return String.valueOf(date.getTime());
	}

	@Override
	public Date parse(String text, Locale locale) throws ParseException {
		long time = NumberUtils.parseNumber(text, Long.class);
		if (time > 0) 
			return new Date(time);
		else 
			return DateTimeUtil.DEFAULT_DATE_MIN;
	}
}
