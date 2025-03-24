package com.example.demo.async.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Tuple2<T1, T2> {

    private T1 t1;
    private T2 t2;
}
