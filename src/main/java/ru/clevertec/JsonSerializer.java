package ru.clevertec;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class JsonSerializer {

    public static String toJson(Object obj) throws IllegalAccessException {
        return serializeObject(obj);
    }

    private static String serializeObject(Object obj) throws IllegalAccessException {
        StringBuilder json = new StringBuilder();

        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();

//        json.append(clazz.getSimpleName());
        json.append("{");
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            Object value = field.get(obj);

            json.append("\"").append(field.getName()).append("\":");
            if (value == null) {
                json.append("null");
            } else if (value instanceof List<?>) {
                json.append(serializeList((List<?>) value));
            } else if (value instanceof Map<?, ?>) {
                json.append(serializeMap((Map<?, ?>) value));
            } else if (clazz.isPrimitive() || isStandardJavaClass(value.getClass())) {
                json.append("\"").append(value).append("\"");
            } else {
                json.append(serializeObject(value));
            }

            if (i < fields.length - 1) {
                json.append(",");
            }
        }
        json.append("}");
        return json.toString();
    }

    private static boolean isStandardJavaClass(Class<?> clazz) {
        return clazz.getPackage().getName().startsWith("java.");
    }

    private static String serializeList(List<?> list) throws IllegalAccessException {
        StringBuilder json = new StringBuilder();
        json.append("[");
        for (int i = 0; i < list.size(); i++) {
            Object item = list.get(i);
            if (item == null) {
                json.append("null");
            } else if (item.getClass().isPrimitive() || isStandardJavaClass(item.getClass())) {
                json.append("\"").append(item).append("\"");
            } else {
                json.append(serializeObject(item));
            }

            if (i < list.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }
    private static String serializeMap(Map<?, ?> map) throws IllegalAccessException {
        StringBuilder json = new StringBuilder();
        json.append("{");
        int i = 0;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            json.append("\"").append(entry.getKey().toString()).append("\":");
            Object value = entry.getValue();
            if (value == null) {
                json.append("null");
            } else if (value.getClass().isPrimitive() || isStandardJavaClass(value.getClass())) {
                json.append("\"").append(value).append("\"");
            } else {
                json.append(serializeObject(value));
            }

            if (i < map.size() - 1) {
                json.append(",");
            }
            i++;
        }
        json.append("}");
        return json.toString();
    }
}
