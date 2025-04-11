package io.github.toolkit.cache.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.GenericServlet;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class IOUtils {

    protected static final Logger logger = LoggerFactory.getLogger(IOUtils.class);
    public static final int DEFAULT_BUFFER_SIZE = 4096;
    private static Date startTime;

    public static String read(InputStream in) {
        InputStreamReader reader;
        try {
            reader = new InputStreamReader(in, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }

        return read(reader);
    }

    public static String readFromResource(String resource) {
        InputStream inputStream = null;

        try {
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
            if (inputStream == null) {
                inputStream = IOUtils.class.getResourceAsStream(resource);
            }

            if (inputStream == null) {
                return null;
            }

            return read(inputStream);
        } finally {
            close(inputStream);
        }
    }

    public static byte[] readByteArrayFromResource(String resource) throws IOException {
        InputStream inputStream = null;

        try {
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
            if (inputStream != null) {
                return readByteArray(inputStream);
            }
        } finally {
            close(inputStream);
        }
        return null;
    }

    public static byte[] readByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    public static long copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[4096];
        long count = 0L;

        for (int n; -1 != (n = input.read(buffer)); count += n) {
            output.write(buffer, 0, n);
        }

        return count;
    }

    public static String read(Reader reader) {
        try {
            StringWriter writer = new StringWriter();
            char[] buffer = new char[4096];

            int n;
            while (-1 != (n = reader.read(buffer))) {
                writer.write(buffer, 0, n);
            }

            return writer.toString();
        } catch (IOException e) {
            throw new IllegalStateException("read error", e);
        }
    }

    public static String read(Reader reader, int length) {
        try {
            char[] buffer = new char[length];
            int offset = 0;
            int rest = length;

            int len;
            while ((len = reader.read(buffer, offset, rest)) != -1) {
                rest -= len;
                offset += len;
                if (rest == 0) {
                    break;
                }
            }

            return new String(buffer, 0, length - rest);
        } catch (IOException e) {
            throw new IllegalStateException("read error", e);
        }
    }

    public static String toString(Date date) {
        if (date == null) {
            return null;
        } else {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return format.format(date);
        }
    }

    public static String getStackTrace(Throwable ex) {
        StringWriter buf = new StringWriter();
        ex.printStackTrace(new PrintWriter(buf));
        return buf.toString();
    }

    public static String toString(StackTraceElement[] stackTrace) {
        StringBuilder buf = new StringBuilder();
        StackTraceElement[] var2 = stackTrace;
        int var3 = stackTrace.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            StackTraceElement item = var2[var4];
            buf.append(item.toString());
            buf.append("\n");
        }

        return buf.toString();
    }

    public static Boolean getBoolean(Properties properties, String key) {
        String property = properties.getProperty(key);
        if ("true".equals(property)) {
            return Boolean.TRUE;
        } else {
            return "false".equals(property) ? Boolean.FALSE : null;
        }
    }

    public static Boolean getBoolean(GenericServlet servlet, String key) {
        String property = servlet.getInitParameter(key);
        if ("true".equals(property)) {
            return Boolean.TRUE;
        } else {
            return "false".equals(property) ? Boolean.FALSE : null;
        }
    }

    public static Integer getInteger(Properties properties, String key) {
        String property = properties.getProperty(key);
        if (property == null) {
            return null;
        } else {
            try {
                return Integer.parseInt(property);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

    public static Long getLong(Properties properties, String key) {
        String property = properties.getProperty(key);
        if (property == null) {
            return null;
        } else {
            try {
                return Long.parseLong(property);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

    public static Class<?> loadClass(String className) {
        Class<?> clazz = null;
        if (className == null) {
            return null;
        } else {
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException e) {
                ClassLoader ctxClassLoader = Thread.currentThread().getContextClassLoader();
                if (ctxClassLoader != null) {
                    try {
                        clazz = ctxClassLoader.loadClass(className);
                    } catch (ClassNotFoundException e2) {
                    }
                }

                return clazz;
            }
        }
    }

    public static Date getStartTime() {
        if (startTime == null) {
            startTime = new Date(ManagementFactory.getRuntimeMXBean().getStartTime());
        }

        return startTime;
    }

    public static long murmurhash2_64(String text) {
        byte[] bytes = text.getBytes();
        return murmurhash2_64(bytes, bytes.length, -512093083);
    }

    public static long murmurhash2_64(byte[] data, int length, int seed) {
        long h = seed & 4294967295L ^ length * -4132994306676758123L;
        int length8 = length / 8;

        for (int i = 0; i < length8; ++i) {
            int i8 = i * 8;
            long k = (data[i8 + 0] & 255L) + ((data[i8 + 1] & 255L) << 8) + ((data[i8 + 2] & 255L) << 16) + ((data[i8 + 3] & 255L) << 24) + ((data[i8 + 4] & 255L) << 32) + ((data[i8 + 5] & 255L) << 40) + ((data[i8 + 6] & 255L) << 48) + ((data[i8 + 7] & 255L) << 56);
            k *= -4132994306676758123L;
            k ^= k >>> 47;
            k *= -4132994306676758123L;
            h ^= k;
            h *= -4132994306676758123L;
        }

        switch (length % 8) {
            case 7:
                h ^= (long) (data[(length & -8) + 6] & 255) << 48;
            case 6:
                h ^= (long) (data[(length & -8) + 5] & 255) << 40;
            case 5:
                h ^= (long) (data[(length & -8) + 4] & 255) << 32;
            case 4:
                h ^= (long) (data[(length & -8) + 3] & 255) << 24;
            case 3:
                h ^= (long) (data[(length & -8) + 2] & 255) << 16;
            case 2:
                h ^= (long) (data[(length & -8) + 1] & 255) << 8;
            case 1:
                h ^= (long) (data[length & -8] & 255);
                h *= -4132994306676758123L;
            default:
                h ^= h >>> 47;
                h *= -4132994306676758123L;
                h ^= h >>> 47;
                return h;
        }
    }

    public static byte[] md5Bytes(String text) {
        MessageDigest msgDigest = null;

        try {
            msgDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException var3) {
            throw new IllegalStateException("System doesn't support MD5 algorithm.");
        }

        msgDigest.update(text.getBytes());
        byte[] bytes = msgDigest.digest();
        return bytes;
    }

    public static void putLong(byte[] b, int off, long val) {
        b[off + 7] = (byte) ((int) (val >>> 0));
        b[off + 6] = (byte) ((int) (val >>> 8));
        b[off + 5] = (byte) ((int) (val >>> 16));
        b[off + 4] = (byte) ((int) (val >>> 24));
        b[off + 3] = (byte) ((int) (val >>> 32));
        b[off + 2] = (byte) ((int) (val >>> 40));
        b[off + 1] = (byte) ((int) (val >>> 48));
        b[off + 0] = (byte) ((int) (val >>> 56));
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                logger.debug("close error", e);
            }
        }
    }
}