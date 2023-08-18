package com.lbn.companion.dataprocess.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class Word implements Serializable {


    private String word;
    private int count;

}
