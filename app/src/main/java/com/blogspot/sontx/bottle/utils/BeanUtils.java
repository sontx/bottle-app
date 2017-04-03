package com.blogspot.sontx.bottle.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

import lombok.NonNull;

public final class BeanUtils {
    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
    }

    private BeanUtils() {
    }

    public static Map<String, Object> toMap(@NonNull Object bean) {
        Map map = objectMapper.convertValue(bean, Map.class);
        return map;
    }
}
