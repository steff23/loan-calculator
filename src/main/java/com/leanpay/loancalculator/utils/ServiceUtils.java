package com.leanpay.loancalculator.utils;

import com.leanpay.loancalculator.model.request.loan.LoanRequest;
import com.leanpay.loancalculator.model.response.amortization.AmortizationResponse;
import com.leanpay.loancalculator.model.response.amortization.AmortizationSchedule;
import com.leanpay.loancalculator.model.response.loan.LoanResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class ServiceUtils {
    public static double roundNumbers(double number, int numberOfDigits) {
        BigDecimal roundedNumber = new BigDecimal(Double.toString(number)).setScale(numberOfDigits, RoundingMode.HALF_UP);
        return roundedNumber.doubleValue();
    }

    public static AmortizationSchedule buildAmortizationSchedule(int order, double paymentAmount, double principalAmount, double interestAmount, double balanceOwed) {
        return AmortizationSchedule.builder()
                .id(order)
                .paymentAmount(paymentAmount)
                .principalAmount(principalAmount)
                .interestAmount(interestAmount)
                .balanceOwed(balanceOwed)
                .build();
    }

    public static AmortizationResponse buildAmortizationResponse(double loanAmount, List<AmortizationSchedule> schedules) {
        return AmortizationResponse.builder()
                .loanAmount(loanAmount)
                .amortizationSchedule(schedules)
                .build();
    }

    public static LoanResponse buildLoanResponse(double monthlyPayment, double totalInterestPaid) {
        return LoanResponse.builder()
                .monthlyPayment(monthlyPayment)
                .totalInterestPaid(totalInterestPaid)
                .build();
    }
}
