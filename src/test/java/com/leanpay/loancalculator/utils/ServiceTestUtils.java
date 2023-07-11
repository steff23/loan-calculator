package com.leanpay.loancalculator.utils;

import com.leanpay.loancalculator.model.enums.DurationPeriod;
import com.leanpay.loancalculator.model.enums.PaymentFrequency;
import com.leanpay.loancalculator.model.request.amortization.AmortizationRequest;
import com.leanpay.loancalculator.model.request.loan.LoanRequest;
import com.leanpay.loancalculator.model.request.loan.LoanTerm;

import java.math.BigDecimal;

public class ServiceTestUtils {
    public static LoanRequest buildLoanRequest(BigDecimal loanAmount, BigDecimal interestRate, LoanTerm loanTerm) {
        return LoanRequest.builder()
                .loanAmount(loanAmount)
                .interestRate(interestRate)
                .loanTerm(loanTerm)
                .build();
    }

    public static AmortizationRequest buildAmortizationRequest(BigDecimal loanAmount, BigDecimal interestRate, int numberOfPayments, PaymentFrequency paymentFrequency) {
        return AmortizationRequest.builder()
                .loanAmount(loanAmount)
                .interestRate(interestRate)
                .numberOfPayments(numberOfPayments)
                .paymentFrequency(paymentFrequency)
                .build();
    }

    public static LoanTerm buildLoanTerm(int duration, DurationPeriod durationPeriod) {
        return LoanTerm.builder()
                .duration(duration)
                .durationPeriod(durationPeriod)
                .build();
    }
}
