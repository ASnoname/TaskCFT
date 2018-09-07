package com.github.ASnoname.TaskCFT.configure.comparators;

import java.util.Comparator;

public class StringComparator implements Comparator<String> {
    public StringComparator() {
    }

    @Override
    public int compare(String o1, String o2) {
        return o1.compareTo(o2);
    }
}
