package com.homework.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class CollectionInnerClass implements Serializable {

    private String string;
    private SerializeEnum serializeEnum;
}
