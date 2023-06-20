package com.leanpay.loancalculator.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentFrequency {
    DAILY(365),
    WEEKLY(52),
    BIWEEKLY(26),
    SEMI_MONTH(24),
    MONTHLY(12),
    BIMONTHLY(6),
    QUARTERLY(4),
    SEMI_ANNUAL(2),
    ANNUAL(1);

    public final int frequencyPerYear;
}
