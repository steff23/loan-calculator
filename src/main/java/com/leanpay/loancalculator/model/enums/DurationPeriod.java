package com.leanpay.loancalculator.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DurationPeriod {
    YEARS(1),
    MONTHS(12);

    private final int durationPerYear;
}
