package com.leanpay.loancalculator.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ServiceUtils {
    public static double roundNumbers(double number, int numberOfDigits) {
        BigDecimal roundedNumber = new BigDecimal(Double.toString(number)).setScale(numberOfDigits, RoundingMode.HALF_UP);
        return roundedNumber.doubleValue();
    }
}
