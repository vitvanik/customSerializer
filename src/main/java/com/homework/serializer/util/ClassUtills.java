package com.homework.serializer.util;

import java.util.*;

public class ClassUtills {

    private static final List<Class<?>> primitiveWrapperTypes = Arrays.asList(
            Boolean.class,
            Byte.class,
            Character.class,
            Double.class,
            Float.class,
            Integer.class,
            Long.class,
            Short.class,
            String.class
    );

    public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        Objects.requireNonNull(clazz, "Class must not be null");
        return clazz.isPrimitive() || isPrimitiveWrapper(clazz);
    }

    public static boolean isPrimitiveWrapper(Class<?> clazz) {
        Objects.requireNonNull(clazz, "Class must not be null");
        return primitiveWrapperTypes.contains(clazz);
    }

    public static boolean isCollection(Class<?> clazz) {
        Objects.requireNonNull(clazz, "Class must not be null");
        return Collection.class.isAssignableFrom(clazz);
    }

    public static boolean isMap(Class<?> clazz) {
        Objects.requireNonNull(clazz, "Class must not be null");
        return Map.class.isAssignableFrom(clazz);
    }

    public static boolean isEnum(Class<?> clazz) {
        Objects.requireNonNull(clazz, "Class must not be null");
        return clazz.isEnum();
    }
}
