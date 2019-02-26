package com.homework;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.homework.entity.InnerClass;
import com.homework.entity.SerializeClass;
import com.homework.entity.SerializeProtoClassOuterClass;
import com.homework.serializer.Serializer;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;

import java.io.*;
import java.util.*;

public class BenchmarkRunner {

    private static Serializer serializer;

    private static Kryo kryo;

    private static ObjectMapper objectMapper;

    private static SerializeClass serializeClass;

    private static SerializeProtoClassOuterClass.SerializeProtoClass serializeProtoClass;

    {
        serializer = new Serializer();

        kryo = new Kryo();

        objectMapper = new ObjectMapper();

        final Set<String> stringSet = new HashSet<>();
        stringSet.add("setString");
        stringSet.add("setString1");

        final List<InnerClass> listInnerClasses = new ArrayList<>();
        listInnerClasses.add(new InnerClass().setString("innerString1").setString1("innerString2"));
        listInnerClasses.add(new InnerClass().setString("innerString3").setString1("innerString4"));

        final Map<String, String> map = new HashMap<>();
        map.put("s1", "a1");
        map.put("s2", "a2");

        final InnerClass innerClass = new InnerClass()
                .setString("innerClassString1")
                .setString1("innerClassString2");

        serializeClass = new SerializeClass()
                .setInteger(new Random().nextInt())
                .setString("string")
                .setInnerClass(new InnerClass().setString("stringInnerClass").setString1("string1InnerClass"))
                .setSetStrings(stringSet)
                .setListInnerClasses(listInnerClasses)
                .setMap(map)
                .setInnerClass(innerClass);

        SerializeProtoClassOuterClass.InnerClass innerClass1 = SerializeProtoClassOuterClass.InnerClass.newBuilder()
                .setString("string")
                .setString1("string1")
                .build();

        final List<SerializeProtoClassOuterClass.InnerClass> listInnerClasses1 = new ArrayList<>();
        listInnerClasses1.add(SerializeProtoClassOuterClass.InnerClass.newBuilder()
                .setString("string")
                .setString1("string1")
                .build());
        listInnerClasses1.add(SerializeProtoClassOuterClass.InnerClass.newBuilder()
                .setString("string")
                .setString1("string1")
                .build());

        serializeProtoClass = SerializeProtoClassOuterClass.SerializeProtoClass.newBuilder()
                .setInteger(new Random().nextInt())
                .setString("string")
                .setInnerClass(innerClass1)
                .addAllSetStrings(stringSet)
                .addAllListInnerClasses(listInnerClasses1)
                .putAllMap(map)
                .build();
    }

    public static void main(String[] args) throws Exception {
        Main.main(args);
    }

    @Benchmark
    @Fork(value = 1)
    @BenchmarkMode(Mode.Throughput)
    public void mySerializer() throws  Exception {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            serializer.serialization(bos, serializeClass);
            SerializeClass newSerializeClass = (SerializeClass) serializer.deserialization(bos.toByteArray(), SerializeClass.class);
        }
    }

    @Benchmark
    @Fork(value = 1)
    @BenchmarkMode(Mode.Throughput)
    public void kryo() throws Exception {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            Output output = new Output(bos);
            kryo.writeClassAndObject(output, serializeClass);
            final Input input = new Input(output.getBuffer());
            SerializeClass newSerializeClass = (SerializeClass) kryo.readClassAndObject(input);
        }
    }

    @Benchmark
    @Fork(value = 1)
    @BenchmarkMode(Mode.Throughput)
    public void java() throws Exception {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(serializeClass);

            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bis);
            SerializeClass newSerializeClass = (SerializeClass) ois.readObject();
        }
    }

    @Benchmark
    @Fork(value = 1)
    @BenchmarkMode(Mode.Throughput)
    public void json() throws Exception {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            objectMapper.writeValue(bos, serializeClass);
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            SerializeClass newSerializeClass = objectMapper.readValue(bis, SerializeClass.class);
        }
    }

    @Benchmark
    @Fork(value = 1)
    @BenchmarkMode(Mode.Throughput)
    public void protobuf() throws Exception {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            serializeProtoClass.writeTo(bos);
            SerializeProtoClassOuterClass.SerializeProtoClass newSerializeClass = SerializeProtoClassOuterClass.SerializeProtoClass.newBuilder().mergeFrom(bos.toByteArray()).build();
        }
    }
}