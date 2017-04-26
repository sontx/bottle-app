package com.blogspot.sontx.bottle.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Field;
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

    public static void updateBean(String name, Object value, Object to) {
        Class<?> toClass = to.getClass();
        Field[] fields = toClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equalsIgnoreCase(name)) {
                if (!field.isAccessible())
                    field.setAccessible(true);
                try {
                    field.set(to, value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }

    }
}
