package com.project.wallet_keeper.entity;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum MonthName {
    JANUARY(1, "January"),
    FEBRUARY(2, "February"),
    MARCH(3, "March"),
    APRIL(4, "April"),
    MAY(5, "May"),
    JUNE(6, "June"),
    JULY(7, "July"),
    AUGUST(8, "August"),
    SEPTEMBER(9, "September"),
    OCTOBER(10, "October"),
    NOVEMBER(11, "November"),
    DECEMBER(12, "December");

    private static final Map<Integer, String> MAP = Arrays.stream(values())
            .collect(Collectors.toMap(MonthName::getMonth, MonthName::getName));

    private final int month;
    private final String name;

    MonthName(int month, String name) {
        this.month = month;
        this.name = name;
    }

    public static String getMonthName(int month) {
        return MAP.get(month);
    }
}
