package com.leanpay.loancalculator.service;

import com.leanpay.loancalculator.model.request.amortization.AmortizationRequest;
import com.leanpay.loancalculator.model.response.amortization.AmortizationResponse;
import com.leanpay.loancalculator.model.response.amortization.AmortizationSchedule;
import com.leanpay.loancalculator.repository.AmortizationCalculationRepository;
import com.leanpay.loancalculator.repository.AmortizationCalculationResultRepository;
import com.leanpay.loancalculator.utils.ServiceUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class AmortizationCalculationService {

    private final AmortizationCalculationRepository amortizationCalculationRepository;
    private final AmortizationCalculationResultRepository amortizationCalculationResultRepository;
    public AmortizationResponse calculateAmortizationObligations(AmortizationRequest amortizationRequest) {

        double periodicalInterestRate = calculatePeriodicalInterestRate(amortizationRequest);

        double periodicalPayment = calculatePeriodicalPayment(amortizationRequest.getLoanAmount(), periodicalInterestRate, amortizationRequest.getNumberOfPayments());
        double remainingBalance = amortizationRequest.getLoanAmount();

        List<AmortizationSchedule> schedules = new ArrayList<>();
        for(int paymentNumber = 1; paymentNumber < amortizationRequest.getNumberOfPayments(); paymentNumber ++) {
            remainingBalance = getRemainingBalance(periodicalInterestRate, periodicalPayment, remainingBalance, schedules, paymentNumber);
        }
        AmortizationResponse response = buildAmortizationResponse(amortizationRequest.getLoanAmount(), schedules);

        amortizationCalculationRepository.save(amortizationRequest);
        amortizationCalculationResultRepository.save(response);

        return response;
    }

    private double calculatePeriodicalInterestRate(AmortizationRequest amortizationRequest) {
        int paymentFrequency = amortizationRequest.getPaymentFrequency().getFrequencyPerYear();
        double periodicalInterestRate =  amortizationRequest.getInterestRate() / 100 / paymentFrequency;
        return ServiceUtils.roundNumbers(periodicalInterestRate,6);
    }

    private double getRemainingBalance(double roundedPeriodicalInterestRate, double roundedPeriodicalPayment, double remainingBalance, List<AmortizationSchedule> schedules, int paymentNumber) {
        double interestPayment = remainingBalance * roundedPeriodicalInterestRate;
        double roundedInterestPayment = ServiceUtils.roundNumbers(interestPayment, 2);
        double principalPayment = roundedPeriodicalPayment - roundedInterestPayment;
        remainingBalance -= principalPayment;
        AmortizationSchedule schedule = buildAmortizationSchedule(paymentNumber, roundedPeriodicalPayment, principalPayment, roundedInterestPayment, remainingBalance);
        schedules.add(schedule);
        return remainingBalance;
    }

    private double calculatePeriodicalPayment(double loanAmount, double periodicalInterestRate, int numberOfPayments) {
        double numerator = periodicalInterestRate * loanAmount;
        double denominator = 1 - Math.pow(1 + periodicalInterestRate, -numberOfPayments);
        double periodicalPayment = numerator/denominator;
        return ServiceUtils.roundNumbers(periodicalPayment, 2);
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
