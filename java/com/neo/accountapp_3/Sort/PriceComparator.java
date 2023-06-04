package com.neo.accountapp_3.Sort;

import java.util.Comparator;

//가격을 정렬할때 사용한다.
public class PriceComparator implements Comparator<PriceSort> {
    @Override
    public int compare(PriceSort a1, PriceSort a2) {
        if (a1.price > a2.price) {
            return 1;
        } else if (a1.price < a2.price) {
            return -1;
        }
        return 0;
    }
}
