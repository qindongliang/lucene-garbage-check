package com.anytrust.model;

import java.util.ArrayList;

/**
 * Created by qindongliang on 2016/2/3.
 *  垃圾过滤实体类载体
 */
public class Word {

    /**词库主词*/
    private String word;
    /**查询时的词间距,默认为20**/
    private Integer distance=20;
    /**分词后的位置数组*/
    private ArrayList<Integer> ords =new ArrayList<Integer>();


    public Word(String word, Integer distance) {
        this.word = word;
        this.distance = distance;
    }


    public ArrayList<Integer> getOrds() {
        return ords;
    }

    public void setOrds(ArrayList<Integer> ords) {
        this.ords = ords;
    }

    @Override
    public String toString() {
        return "Word{" +
                "word='" + word + '\'' +
                ", distance=" + distance +
                '}';
    }

    public Word() {
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }
}
