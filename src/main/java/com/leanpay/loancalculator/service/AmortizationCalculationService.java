package com.leanpay.loancalculator.service;

import com.leanpay.loancalculator.model.request.amortization.AmortizationRequest;
import com.leanpay.loancalculator.model.response.amortization.AmortizationResponse;
import com.leanpay.loancalculator.model.response.amortization.AmortizationSchedule;
import com.leanpay.loancalculator.repository.AmortizationCalculationRepository;
import com.leanpay.loancalculator.repository.AmortizationCalculationResultRepository;
import com.leanpay.loancalculator.utils.ServiceUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class AmortizationCalculationService {

    private final AmortizationCalculationRepository amortizationCalculationRepository;
    private final AmortizationCalculationResultRepository amortizationCalculationResultRepository;

    @Transactional
    public ResponseEntity<AmortizationResponse> calculateAmortizationObligations(AmortizationRequest amortizationRequest) {

        BigDecimal periodicalInterestRate = calculatePeriodicalInterestRate(amortizationRequest);

        BigDecimal periodicalPayment = calculatePeriodicalPayment(amortizationRequest.getLoanAmount(), periodicalInterestRate, amortizationRequest.getNumberOfPayments());
        BigDecimal roundedPeriodicalPayment = ServiceUtils.roundNumbers(periodicalPayment, 2);
        BigDecimal remainingBalance = amortizationRequest.getLoanAmount();

        List<AmortizationSchedule> schedules = new ArrayList<>();
        for (int paymentNumber = 1; paymentNumber <= amortizationRequest.getNumberOfPayments(); paymentNumber++) {
            remainingBalance = getRemainingBalance(periodicalInterestRate, roundedPeriodicalPayment, remainingBalance, schedules, paymentNumber);
        }
        AmortizationResponse response = ServiceUtils.buildAmortizationResponse(amortizationRequest.getLoanAmount(), schedules);

        amortizationCalculationRepository.save(amortizationRequest);
        amortizationCalculationResultRepository.save(response);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Transactional
    public ResponseEntity<List<AmortizationResponse>> listAllAmortizations() {
        List<AmortizationResponse> amortizationResponses = amortizationCalculationResultRepository.findAll();
        return ResponseEntity.ok(amortizationResponses);
    }

    private BigDecimal calculatePeriodicalInterestRate(AmortizationRequest amortizationRequest) {
        int paymentFrequency = amortizationRequest.getPaymentFrequency().getFrequencyPerYear();
        BigDecimal periodicalInterestRate = amortizationRequest.getInterestRate().divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP).divide(new BigDecimal(String.valueOf(paymentFrequency)), 6, RoundingMode.HALF_UP);
        return ServiceUtils.roundNumbers(periodicalInterestRate, 6);
    }

    private BigDecimal getRemainingBalance(BigDecimal roundedPeriodicalInterestRate, BigDecimal roundedPeriodicalPayment, BigDecimal remainingBalance, List<AmortizationSchedule> schedules, int paymentNumber) {
        BigDecimal interestPayment = remainingBalance.multiply(roundedPeriodicalInterestRate);
        BigDecimal roundedInterestPayment = ServiceUtils.roundNumbers(interestPayment, 2);
        BigDecimal principalPayment = ServiceUtils.roundNumbers((roundedPeriodicalPayment.subtract(roundedInterestPayment)), 2);
        remainingBalance = remainingBalance.subtract(principalPayment);
        BigDecimal roundedRemainingBalance = ServiceUtils.roundNumbers(remainingBalance, 2);
        AmortizationSchedule schedule = ServiceUtils.buildAmortizationSchedule(paymentNumber, roundedPeriodicalPayment, principalPayment, roundedInterestPayment, roundedRemainingBalance);
        schedules.add(schedule);
        return roundedRemainingBalance;
    }

    private BigDecimal calculatePeriodicalPayment(BigDecimal loanAmount, BigDecimal periodicalInterestRate, int numberOfPayments) {
        BigDecimal numerator = periodicalInterestRate.multiply(loanAmount);
        BigDecimal base = periodicalInterestRate.add(BigDecimal.ONE);
        BigDecimal denominator = BigDecimal.ONE.subtract(base.pow(-numberOfPayments));
        BigDecimal periodicalPayment = numerator.divide(denominator, RoundingMode.HALF_UP);
        return ServiceUtils.roundNumbers(periodicalPayment, 2);
    }
}
