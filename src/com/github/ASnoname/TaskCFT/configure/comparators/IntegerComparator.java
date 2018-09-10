package com.github.ASnoname.TaskCFT.configure.comparators;

import java.util.Comparator;

public class IntegerComparator implements Comparator<String> {

    public IntegerComparator() {
    }

    @Override
    public int compare(String o1, String o2) {

        if (o1.length() > o2.length()){
            return 1;
        }
        if (o2.length() > o1.length()){
            return -1;
        }
        for (int k = 0; k < o1.length(); k++) {
            char c1 = o1.charAt(k);
            char c2 = o2.charAt(k);
            if (c1 > c2) return 1;
            if (c1 < c2) return -1;
        }
        return 0;
    }
}