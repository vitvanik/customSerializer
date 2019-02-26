package com.homework.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class SerializeClass implements Serializable {

    private Integer integer;
    private String string;
    private InnerClass innerClass;
    private List<InnerClass> listInnerClasses;
    private Set<String> setStrings;
    private Map<String, String> map;
}
