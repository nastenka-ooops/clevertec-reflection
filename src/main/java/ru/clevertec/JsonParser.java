package ru.clevertec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonParser {

    public static Map<String, Object> parseJsonToMap(String json) {
        // Удаление внешних фигурных скобок
        json = json.trim();
        if (json.startsWith("{")) {
            json = json.substring(1, json.length() - 1);
        }

        Map<String, Object> resultMap = new HashMap<>();
        String[] keyValuePairs = splitJson(json);

        for (String pair : keyValuePairs) {
            String[] keyValue = pair.split(":", 2);
            String key = cleanString(keyValue[0].trim());
            String value = keyValue[1].trim();

            if (value.startsWith("{")) {
                // Вложенный объект
                resultMap.put(key, parseJsonToMap(value));
            } else if (value.startsWith("[")) {
                // Массив
                resultMap.put(key, parseJsonToList(value));
            } else if (value.startsWith("\"")) {
                // Строка
                resultMap.put(key, cleanString(value));
            } else if (value.equals("null")) {
                // null значение
                resultMap.put(key, null);
            } else if (value.equals("true") || value.equals("false")) {
                // Булево значение
                resultMap.put(key, Boolean.parseBoolean(value));
            } else {
                // Число (Integer или Double)
                resultMap.put(key, parseNumber(value));
            }
        }

        return resultMap;
    }

    private static String cleanString(String value) {
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    private static Object parseNumber(String value) {
        try {
            if (value.contains(".")) {
                return Double.parseDouble(value);
            } else {
                return Integer.parseInt(value);
            }
        } catch (NumberFormatException e) {
            return value; // Если это не число, оставляем как строку
        }
    }

    private static String[] splitJson(String json) {
        List<String> result = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        boolean inQuotes = false;
        int brackets = 0;
        int squareBrackets = 0;

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);

            if (c == '\"') {
                inQuotes = !inQuotes; // Переключаем режим кавычек
            }

            if (!inQuotes) {
                if (c == '{') brackets++;
                if (c == '}') brackets--;
                if (c == '[') squareBrackets++;
                if (c == ']') squareBrackets--;
            }

            if (c == ',' && !inQuotes && brackets == 0 && squareBrackets == 0) {
                result.add(buffer.toString().trim());
                buffer = new StringBuilder();
            } else {
                buffer.append(c);
            }
        }

        result.add(buffer.toString().trim());
        return result.toArray(new String[0]);
    }

    public static List<Object> parseJsonToList(String json) {
        json = json.trim();
        if (json.startsWith("[")) {
            json = json.substring(1, json.length() - 1);
        }

        List<Object> resultList = new ArrayList<>();
        String[] values = splitJson(json);

        for (String value : values) {
            value = value.trim();
            if (value.startsWith("{")) {
                // Вложенный объект
                resultList.add(parseJsonToMap(value));
            } else if (value.startsWith("[")) {
                // Вложенный массив
                resultList.add(parseJsonToList(value));
            } else if (value.startsWith("\"")) {
                // Строка
                resultList.add(cleanString(value));
            } else if (value.equals("null")) {
                // null значение
                resultList.add(null);
            } else if (value.equals("true") || value.equals("false")) {
                // Булево значение
                resultList.add(Boolean.parseBoolean(value));
            } else {
                // Число (Integer или Double)
                resultList.add(parseNumber(value));
            }
        }

        return resultList;
    }

}
