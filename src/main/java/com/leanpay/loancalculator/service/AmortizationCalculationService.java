package com.leanpay.loancalculator.service;

import com.leanpay.loancalculator.model.request.amortization.AmortizationRequest;
import com.leanpay.loancalculator.model.response.amortization.AmortizationResponse;
import com.leanpay.loancalculator.model.response.amortization.AmortizationSchedule;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AmortizationCalculationService {
    public AmortizationResponse calculateAmortizationObligations(AmortizationRequest amortizationRequest) {
        double periodicalInterestRate = amortizationRequest.getInterestRate() / 100 / amortizationRequest.getPaymentFrequency().getFrequencyPerYear();
        double periodicalPayment = calculatePeriodicalPayment(amortizationRequest.getLoanAmount(), periodicalInterestRate, amortizationRequest.getNumberOfPayments());
        double remainingBalance = amortizationRequest.getLoanAmount();

        List<AmortizationSchedule> schedules = new ArrayList<>();
        for(int paymentNumber = 1; paymentNumber < amortizationRequest.getNumberOfPayments(); paymentNumber ++) {
            double interestPayment = remainingBalance * periodicalInterestRate;
            double principalPayment = periodicalPayment - interestPayment;
            remainingBalance -= principalPayment;
            AmortizationSchedule schedule = buildAmortizationSchedule(paymentNumber, periodicalPayment, principalPayment, interestPayment, remainingBalance);
            schedules.add(schedule);
        }
        return buildAmortizationResponse(amortizationRequest.getLoanAmount(), schedules);
    }

    private double calculatePeriodicalPayment(double loanAmount, double periodicalInterestRate, int numberOfPayments) {
        double numerator = periodicalInterestRate * loanAmount;
        double denominator = 1 - Math.pow(1 + periodicalInterestRate, -numberOfPayments);
        return numerator/denominator;
    }

    private AmortizationSchedule buildAmortizationSchedule(int order, double paymentAmount, double principalAmount, double interestAmount, double balanceOwed) {
        return AmortizationSchedule.builder()
                .id(order)
                .paymentAmount(paymentAmount)
                .principalAmount(principalAmount)
                .interestAmount(interestAmount)
                .balanceOwed(balanceOwed)
                .build();
    }

    private AmortizationResponse buildAmortizationResponse(double loanAmount, List<AmortizationSchedule> schedules) {
        return AmortizationResponse.builder()
                .loanAmount(loanAmount)
                .amortizationSchedule(schedules)
                .build();
    }
}
