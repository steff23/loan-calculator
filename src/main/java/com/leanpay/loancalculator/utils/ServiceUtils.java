package com.leanpay.loancalculator.utils;

import com.leanpay.loancalculator.model.enums.DurationPeriod;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ServiceUtils {
    public static double calculateMonthlyInterestRate(double interestRate) {
        return interestRate / 100 / DurationPeriod.MONTHS.getDurationPerYear();
    }

    public static double getMonthlyPayment(double loanAmount, int numberOfMonths, double interestRatePerMonth) {
        double numerator = loanAmount * interestRatePerMonth * Math.pow((1 + interestRatePerMonth), numberOfMonths);
        double denominator = Math.pow((1 + interestRatePerMonth), numberOfMonths) - 1;
        return numerator/denominator;
    }

    public static double roundNumbers(double number, int numberOfDigits) {
        BigDecimal roundedNumber = new BigDecimal(Double.toString(number)).setScale(numberOfDigits, RoundingMode.HALF_UP);
        return roundedNumber.doubleValue();
    }
}
