package com.homework.serializer.util;

import java.util.Objects;

public class SerializerUtil {

    public static  Object castObject(Class clazz, String value) {
        if(Objects.equals("null", value)) {
            return null;
        }
        switch (clazz.getName()) {
            case "java.lang.Boolean":
                return Boolean.parseBoolean(value);
            case "java.lang.Double":
                return Double.parseDouble(value);
            case "java.lang.Float":
                return Float.parseFloat(value);
            case "java.lang.Integer":
                return Integer.parseInt(value);
            case "java.lang.Long":
                return Long.parseLong(value);
            case "java.lang.Short":
                return Short.parseShort(value);
            default:
                return value;
        }
    }

    public static Class checkType(String type) throws ClassNotFoundException {
            switch (type) {
                case "boolean":
                    return Boolean.class;
                case "double":
                    return Double.class;
                case "float":
                    return Float.class;
                case "int":
                    return Integer.class;
                case "long":
                    return Long.class;
                case "short":
                    return Short.class;
                default:
                    return Class.forName(type);
            }
        }
}
