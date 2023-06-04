package com.neo.accountapp_3.Sort;

import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;

//데이터를 저장하고 comparator에 던져줄때 사용.
public class RegdateSort {
    public String key;
    public String regdate;
    public RegdateSort(String key, String regdate) {
        this.key = key;
        this.regdate = regdate;
    }
    @Override
    public String toString() {
        return "[ " + this.key + ": " + this.regdate + " ]";
    }
}



