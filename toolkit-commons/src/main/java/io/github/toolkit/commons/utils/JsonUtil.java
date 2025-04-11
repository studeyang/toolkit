package io.github.toolkit.commons.utils;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.util.StringUtils;

public class JsonUtil {

    private final static ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        SimpleModule model = new SimpleModule();
        model.addSerializer(Long.class, ToStringSerializer.instance);
        model.addSerializer(Long.TYPE, ToStringSerializer.instance);
        mapper.registerModule(model);

    }

    private static Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    public static <T> String serializer(T obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logger.error("JsonProcessingException-->{}", e);
            return null;
        }
    }

    public static byte[] serializeToBytes(Object obj) {
        try {
            return mapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            logger.error("JsonProcessingException-->{}", e);
            return null;
        }
    }


    public static Map<String, Object> deserialize(String json) {
        return deserialize(json.getBytes());
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> deserialize(byte[] src) {
        return (Map<String, Object>) deserialize(src, Map.class);
    }

    public static <T> T deserialize(String json, Class<T> beanClass) {

        if (!StringUtils.hasText(json)) {
            return null;
        }

        return deserialize(json.getBytes(), beanClass);
    }

    public static <T> T deserialize(byte[] src, Class<T> beanClass) {

        if (src.length == 0 || null == beanClass) {
            return null;
        }

        try {
            return (T) mapper.readValue(src, beanClass);
        } catch (Exception e) {
            logger.error("JsonProcessingException-->{}", e);
            return null;
        }
    }
}
