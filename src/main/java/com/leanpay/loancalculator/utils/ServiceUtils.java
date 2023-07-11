package com.leanpay.loancalculator.utils;

import com.leanpay.loancalculator.model.response.amortization.AmortizationResponse;
import com.leanpay.loancalculator.model.response.amortization.AmortizationSchedule;
import com.leanpay.loancalculator.model.response.loan.LoanResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class ServiceUtils {
    public static BigDecimal roundNumbers(BigDecimal number, int numberOfDigits) {
        return number.setScale(numberOfDigits, RoundingMode.HALF_UP);
    }

    public static AmortizationSchedule buildAmortizationSchedule(int order, BigDecimal paymentAmount, BigDecimal principalAmount, BigDecimal interestAmount, BigDecimal balanceOwed) {
        return AmortizationSchedule.builder()
                .id(order)
                .paymentAmount(paymentAmount)
                .principalAmount(principalAmount)
                .interestAmount(interestAmount)
                .balanceOwed(balanceOwed)
                .build();
    }

    public static AmortizationResponse buildAmortizationResponse(BigDecimal loanAmount, List<AmortizationSchedule> schedules) {
        return AmortizationResponse.builder()
                .loanAmount(loanAmount)
                .amortizationSchedule(schedules)
                .build();
    }

    public static LoanResponse buildLoanResponse(BigDecimal monthlyPayment, BigDecimal totalInterestPaid) {
        return LoanResponse.builder()
                .monthlyPayment(monthlyPayment)
                .totalInterestPaid(totalInterestPaid)
                .build();
    }
}
