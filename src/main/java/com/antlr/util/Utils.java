package com.antlr.util;



import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class Utils {
    public static <T> List<T> filter(Collection<T> data, Predicate<T> pred) {
        if (data == null) return null;
        List<T> filtered = new ArrayList<>();
        for (T x : data) {
            if (pred.test(x)) filtered.add(x);
        }
        return filtered;
    }
}
