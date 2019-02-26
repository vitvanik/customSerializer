package com.homework.serializer;

import com.homework.serializer.util.ClassUtills;
import com.homework.serializer.util.SerializerUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.*;

public class Serializer {

    private static int collectionSeparator = 175;

    private static int objectSeparator = 177;

    private static int baseFieldSeparator = 178;

    public void serialization(OutputStream outputStream, Object o) throws IOException {
        final Map<String, Object> entity = new HashMap<>();
        toMap(o, entity);
        final String packData = pack(entity, baseFieldSeparator);
        outputStream.write(packData.getBytes());
    }

    private void toMap(final Object o, final Map builder) {
        for (Field field : o.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (ClassUtills.isPrimitiveOrWrapper(field.getType())) {
                    builder.put(field.getName(), field.get(o));
                    continue;
                }
                if (ClassUtills.isEnum(field.getType())) {
                    builder.put(field.getName(), field.get(o));
                    continue;
                }
                if (ClassUtills.isMap(field.getType())) {
                    builder.put(field.getName(), field.get(o));
                    continue;
                }
                if (ClassUtills.isCollection(field.getType())) {
                    Class clazz = (Class) ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
                    Collection collection = (Collection) field.get(o);
                    if (ClassUtills.isPrimitiveOrWrapper(clazz)) {
                        final List<Object> collectionData = new ArrayList<>();
                        collection.forEach(item -> {
                            collectionData.add(item);
                        });
                        builder.put(field.getName(), collectionData);
                    } else {
                        final List<Map<String, Object>> serializerMaps = new ArrayList<>();
                        collection.forEach(item -> {
                            final Map<String, Object> serializerMap = new HashMap<>();
                            toMap(item, serializerMap);
                            serializerMaps.add(serializerMap);
                        });
                        builder.put(field.getName(), serializerMaps);
                    }
                    continue;
                }
                final Map<String, Object> innerObject = new HashMap<>();
                toMap(field.get(o), innerObject);
                builder.put(field.getName(), innerObject);
            } catch (IllegalAccessException e) {
                //ignore, because the field accessibility property is set to true
            }
        }
    }

    private String pack(Map<String, Object> o, int fieldSeparator) {
        final List<String> strings = new ArrayList<>();
        for (Map.Entry<String, Object> entry: o.entrySet()) {
            final StringBuilder builder = new StringBuilder();
            if (Objects.isNull(entry.getValue())) {
                builder.append(entry.getKey()).append("==null");
                continue;
            }
            if (ClassUtills.isMap(entry.getValue().getClass())) {
                int nextLevelFieldSeparator = fieldSeparator + 1;
                builder.append(entry.getKey()).append((char) objectSeparator).append(pack((Map) entry.getValue(), nextLevelFieldSeparator));
                strings.add(builder.toString());
                continue;
            }
            if (ClassUtills.isCollection(entry.getValue().getClass())) {
                builder.append(entry.getKey()).append((char) collectionSeparator);
                List<String> collectionValue = new ArrayList<>();
                int nextLevelFieldSeparator = fieldSeparator + 1;
                for (Object o1: (List) entry.getValue()) {
                    if (ClassUtills.isPrimitiveWrapper(o1.getClass())) {
                        collectionValue.add(o1.toString());
                        continue;
                    }
                    collectionValue.add(pack((Map)o1, nextLevelFieldSeparator));
                }
                strings.add(builder.append(String.join(Character.toString((char) objectSeparator), collectionValue)).toString());
                continue;
            }
            builder.append(entry.getKey()).append("==").append(entry.getValue());
            strings.add(builder.toString());
        }
        return String.join(Character.toString((char) fieldSeparator), strings);
    }

    private static void unpack(Object o, String stringObject, int fieldSeparator ) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        for (String fieldAndValue : stringObject.split(Character.toString((char)fieldSeparator))) {
            if (fieldAndValue.contains(Character.toString((char)collectionSeparator))) {
                String[] d = fieldAndValue.split(Character.toString((char)collectionSeparator));
                Field field = o.getClass().getDeclaredField(d[0]);
                field.setAccessible(true);
                final Object fieldObject = createClassFromCollection(field.getType());
                field.set(o, fieldObject);
                ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
                if (ClassUtills.isMap(field.getType())) {

                }
                Class<?> genericTypeCollection = (Class<?>) stringListType.getActualTypeArguments()[0];
                if (ClassUtills.isPrimitiveOrWrapper(genericTypeCollection)) {
                    for (String objectCollection: d[1].split(Character.toString((char)objectSeparator))) {
                        ((Collection) fieldObject).add(SerializerUtil.castObject(genericTypeCollection, objectCollection));
                    }
                    continue;
                } else {
                    int nextFieldSeparator = fieldSeparator + 1;
                    for (String objectCollection: d[1].split(Character.toString((char)objectSeparator))) {
                        final Object innerCollectionObject = Class.forName(genericTypeCollection.getName()).newInstance();
                        unpack(innerCollectionObject, objectCollection, nextFieldSeparator);
                        ((Collection) fieldObject).add(innerCollectionObject);
                    }
                }
                continue;
            }
            if (fieldAndValue.contains(Character.toString((char)objectSeparator))) {
                String[] d = fieldAndValue.split(Character.toString((char)objectSeparator));
                Field field = o.getClass().getDeclaredField(d[0]);
                field.setAccessible(true);
                if (ClassUtills.isMap(field.getType())) {
                    final Object mapObject = createClassFromMap();
                    field.set(o, mapObject);
                    ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
                    Class<?> keyClass = (Class<?>) stringListType.getActualTypeArguments()[0];
                    Class<?> valueClass = (Class<?>) stringListType.getActualTypeArguments()[1];
                    int nextFieldSeparator = fieldSeparator + 1;
                    for (String keyAndValue : d[1].split(Character.toString((char)nextFieldSeparator))) {
                        Object keyObject = SerializerUtil.castObject(keyClass, keyAndValue.split("==")[0]);
                        Object valueObject;
                        if (ClassUtills.isPrimitiveOrWrapper(valueClass)) {
                            valueObject = SerializerUtil.castObject(valueClass, keyAndValue.split("==")[1]);
                        } else {
                            valueObject = Class.forName(keyClass.getName()).newInstance();
                            unpack(valueObject, keyAndValue.split("==")[1], ++nextFieldSeparator);
                        }
                        ((Map) mapObject).put(keyObject, valueObject);
                    }
                    continue;
                }
                final Object fieldObject = Class.forName(field.getType().getName()).newInstance();
                field.set(o, fieldObject);
                int nextFieldSeparator = fieldSeparator + 1;
                unpack(fieldObject, d[1], nextFieldSeparator);
                continue;
            }
            String[] d = fieldAndValue.split("==");
            try {
                Field field = o.getClass().getDeclaredField(d[0]);
                field.setAccessible(true);
                Class type = SerializerUtil.checkType(field.getType().getName());
                if (ClassUtills.isEnum(type)) {
                    field.set(o, Enum.valueOf((Class<? extends Enum>)Class.forName(type.getName()), d[1]));
                } else {
                    field.set(o, SerializerUtil.castObject(type, d[1]));
                }
            } catch (NoSuchFieldException e) {
                throw new NoSuchFieldException(d[0] + "no such");
            } catch (IllegalAccessException e) {
                //ignore, because the field accessibility property is set to true
            }
        }
    }

    public Object deserialization(byte[] data, Class clazz) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException {
        String s = new String(data);
        Object mainClass = clazz.getConstructor().newInstance();
        unpack(mainClass, s, baseFieldSeparator);
        return mainClass;
    }

    private static Collection createClassFromCollection(Class clazz) {
        switch (clazz.getName()) {
            case "java.util.List":
                return new ArrayList<>();
            case "java.util.Set":
                return new HashSet<>();
        }
        return null;
    }

    private static Map createClassFromMap() {
        return new HashMap<>();
    }
}