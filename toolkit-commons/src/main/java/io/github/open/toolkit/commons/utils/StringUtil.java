package io.github.open.toolkit.commons.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by maoxiaodong on 2016/11/9.
 */
public class StringUtil {
	
	private static Logger LOG = LoggerFactory.getLogger(StringUtil.class);

    public static final char UNDERLINE = '_';
    private static final String CHARSET = "UTF-8";

    public static String camelToUnderline(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        char[] charArray = param.toCharArray();
        int len = charArray.length;
        StringBuilder sb = new StringBuilder(len);
        sb.append(charArray[0]);
        for (int i = 1; i < len; i++) {
            char c = charArray[i];
            if (Character.isUpperCase(c)) {
                sb.append(UNDERLINE);
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String underlineToCamel(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (c == UNDERLINE) {
                if (++i < len) {
                    sb.append(Character.toUpperCase(param.charAt(i)));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String classToTableName(Class<?> c) {
        String[] names = c.getName().split("\\.");
        return camelToUnderline(names[names.length - 1]);
    }

    public static int indexFirstUpperCase(String param) {
        char[] arr = param.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] >= 'A' && arr[i] <= 'Z') {
                return i;
            }
        }
        return -1;
    }

    public static Map<String, Integer> findKey(String param, String... keys) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        int lastIndex = Integer.MAX_VALUE;
        for (int i = keys.length - 1; i >= 0; i--) {
            String key = keys[i];
            int index = param.indexOf(key);
            if (index > -1 && index < lastIndex) {
                lastIndex = index;
                map.put(key, index);
            }
        }
        return map;
    }

    public static boolean isEmpty(String s) {
        return ValidateUtil.isEmpty(s);
    }

    public static boolean isNotEmpty(String s) {
        return ValidateUtil.isNotEmpty(s);
    }

    public static String replaceLast(String param, String s1) {
        int index = param.lastIndexOf(s1);
        if (index == -1) {
            return param;
        }
        return param.substring(0, index);
    }

    public static String replaceFinalLast(String param, String s) {
        int index = param.lastIndexOf(s);
        if (index + s.length() == param.length()) {
            return param.substring(0, index);

        }
        return param;

    }

    public static String replaceFirst(String param, String s) {
        int index = param.indexOf(s);
        if (index == 0) {
            return param.substring(s.length(), param.length());

        }
        return param;

    }

    public static void replaceFirst(StringBuffer param, String s) {
        int index = param.indexOf(s);
        if (index == 0) {
            param.replace(0, s.length(), "");
        }

    }

    public static String firstLowerCase(String s) {
        if (isEmpty(s)) {
            return s;
        }
        char[] cs = s.toCharArray();
        cs[0] = Character.toLowerCase(cs[0]);
        StringBuffer buffer = new StringBuffer(cs.length);
        buffer.append(cs);
        return buffer.toString();
    }
    
    /**
     * 效率比较低，慎用
     * @param url
     * @return
     */
    public static String decodeURL(String url) {
    	try {
			url = URLDecoder.decode(url, CHARSET);
		} catch (UnsupportedEncodingException e) {
			LOG.warn("URLDecoder ERROR! ", e);
		}
    	return url;
    }
    
    /**
     * 效率比较低，慎用
     * @param url
     * @return
     */
    public static String encodeURL(String url) {
    	try {
			url = URLEncoder.encode(url, CHARSET);
		} catch (UnsupportedEncodingException e) {
			LOG.warn("URLEncoder ERROR! ", e);
		}
    	return url;
    }
}
