package io.github.toolkit.cache.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.List;

public class FastJSONHelper {

    public static <T> String serialize(T object) {
        return JSON.toJSONString(object);
    }

    public static <T> String serialize(T object, SerializerFeature serializerFeature) {
        return JSON.toJSONString(object, serializerFeature);
    }

    public static <T> T deserialize(String json, Class<T> clz) {
        return JSON.parseObject(json, clz);
    }

    public static <T> List<T> deserializeList(String json, Class<T> clz) {
        return JSON.parseArray(json, clz);
    }

    public static <T> T deserializeAny(String json, TypeReference<T> type) {
        return JSON.parseObject(json, type);
    }
}
