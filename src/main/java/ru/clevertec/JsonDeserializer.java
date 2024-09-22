package ru.clevertec;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;

import static ru.clevertec.JsonParser.parseJsonToMap;

public class JsonDeserializer {

    public static <T> T fromJson(String json, Class<T> clazz) throws Exception {
        Map<String, Object> jsonMap = parseJsonToMap(json);
        return deserializeObject(jsonMap, clazz);
    }

    private static <T> T deserializeObject(Map<String, Object> jsonMap, Class<T> clazz) throws Exception {
        T obj = clazz.getDeclaredConstructor().newInstance();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            Object jsonValue = jsonMap.get(field.getName());

            if (jsonValue == null) {
                field.set(obj, null);
            } else if (isPrimitiveOrWrapper(field.getType()) || field.getType() == String.class) {
                field.set(obj, convertToPrimitive(jsonValue.toString(), field.getType()));
            } else if (field.getType() == List.class) {
                ParameterizedType listType = (ParameterizedType) field.getGenericType();
                Class<?> listClass = (Class<?>) listType.getActualTypeArguments()[0];
                field.set(obj, deserializeList((List<Object>) jsonValue, listClass));
            } else if (field.getType() == Map.class) {
                field.set(obj, deserializeMap((Map<Object, Object>) jsonValue));
            } else if (isStandardJavaClass(field.getType())) {
                field.set(obj, convertToStandardType(jsonValue.toString(), field.getType()));
            } else {
                field.set(obj, deserializeObject((Map<String, Object>) jsonValue, field.getType()));
            }
        }

        return obj;
    }

    private static boolean isStandardJavaClass(Class<?> clazz) {
        return clazz.getPackage().getName().startsWith("java.");
    }

    private static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() ||
                clazz == Integer.class || clazz == Long.class || clazz == Double.class ||
                clazz == Boolean.class || clazz == Byte.class || clazz == Character.class ||
                clazz == Short.class || clazz == Float.class;
    }

    private static Object convertToPrimitive(String value, Class<?> clazz) {
        if (clazz == int.class || clazz == Integer.class) {
            return Integer.parseInt(value);
        } else if (clazz == long.class || clazz == Long.class) {
            return Long.parseLong(value);
        } else if (clazz == double.class || clazz == Double.class) {
            return Double.parseDouble(value);
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (clazz == String.class) {
            return value;
        } else {
            return null;
        }
    }

    private static List<Object> deserializeList(List<Object> jsonList, Class<?> listClass) throws Exception {
        List<Object> list = new ArrayList<>();
        for (Object item : jsonList) {
            if (item instanceof Map) {
                list.add(deserializeObject((Map<String, Object>) item, listClass));
            } else {
                list.add(item);
            }
        }
        return list;
    }

    private static Map<Object, Object> deserializeMap(Map<Object, Object> jsonMap){
        Map<Object, Object> map = new HashMap<>();
        for (Map.Entry<Object, Object> entry : jsonMap.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            map.put(key, value);
        }
        return map;
    }

    private static Object convertToStandardType(String value, Class<?> clazz) {
        if (clazz == UUID.class) {
            return UUID.fromString(value);
        } else if (clazz == LocalDate.class) {
            return LocalDate.parse(value);
        } else if (clazz == OffsetDateTime.class) {
            return OffsetDateTime.parse(value);
        } else if (clazz == BigDecimal.class) {
            return new BigDecimal(value);
        } else {
            return value;
        }
    }
}
