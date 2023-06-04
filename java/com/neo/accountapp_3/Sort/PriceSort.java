package com.neo.accountapp_3.Sort;

import java.util.Comparator;

//데이터를 저장하고 comparator에 던져줄때 사용.
public class PriceSort {
    public String key;
    public int price;
    public PriceSort(String key, int price) {
        this.key = key;
        this.price = price;
    }
    @Override
    public String toString() {
        return "[ " + this.key + ": " + this.price + " ]";
    }
}

