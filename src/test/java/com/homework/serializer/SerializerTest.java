package com.homework.serializer;

import com.homework.entity.InnerClass;
import com.homework.entity.SerializeClass;
import com.homework.entity.CollectionInnerClass;
import com.homework.entity.SerializeEnum;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class SerializerTest {

    private static Serializer serializer;

    private static SerializeClass serializeClass;

    @BeforeAll
    private static void setUp() {

        serializer = new Serializer();

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
    }

    @Test
    @DisplayName("Serializer byte array serialization")
    public void serializerByteArray() throws Exception {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            serializer.serialization(bos, serializeClass);
            SerializeClass newSerializeClass = (SerializeClass) serializer.deserialization(bos.toByteArray(), SerializeClass.class);
            assertFalse(newSerializeClass == serializeClass);
            assertEquals(newSerializeClass.hashCode(), serializeClass.hashCode());
        }
    }
}
