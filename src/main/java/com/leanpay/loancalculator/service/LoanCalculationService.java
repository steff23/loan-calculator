package com.leanpay.loancalculator.service;

import com.leanpay.loancalculator.model.enums.DurationPeriod;
import com.leanpay.loancalculator.model.request.loan.LoanRequest;
import com.leanpay.loancalculator.model.request.loan.LoanTerm;
import com.leanpay.loancalculator.model.response.loan.LoanResponse;
import com.leanpay.loancalculator.repository.LoanCalculationRepository;
import com.leanpay.loancalculator.repository.LoanCalculationResponseRepository;
import com.leanpay.loancalculator.utils.ServiceUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@AllArgsConstructor
public class LoanCalculationService {

    private final LoanCalculationRepository loanCalculationRepository;
    private final LoanCalculationResponseRepository loanCalculationResponseRepository;

    @Transactional
    public ResponseEntity<LoanResponse> calculateLoanPayments(LoanRequest loanRequest) {
        int numberOfPayments = getNumberOfMonths(loanRequest.getLoanTerm());

        BigDecimal interestRatePerMonth = calculateMonthlyInterestRate(loanRequest.getInterestRate());

        BigDecimal monthlyPayment = getMonthlyPayment(loanRequest.getLoanAmount(), numberOfPayments, interestRatePerMonth);

        BigDecimal totalInterestPaid = getTotalInterestPaid(loanRequest, numberOfPayments, monthlyPayment);

        LoanResponse response = ServiceUtils.buildLoanResponse(monthlyPayment, totalInterestPaid);

        loanCalculationRepository.save(loanRequest);
        loanCalculationResponseRepository.save(response);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Transactional
    public ResponseEntity<List<LoanResponse>> listAllLoans() {
        List<LoanResponse> loanResponses = loanCalculationResponseRepository.findAll();
        return ResponseEntity.ok(loanResponses);
    }

    private BigDecimal calculateMonthlyInterestRate(BigDecimal interestRate) {
        BigDecimal monthlyInterestRate = interestRate.divide(new BigDecimal("100.00"), 6, RoundingMode.HALF_UP).divide(new BigDecimal(DurationPeriod.MONTHS.getDurationPerYear()), RoundingMode.HALF_UP);
        return ServiceUtils.roundNumbers(monthlyInterestRate, 6);
    }

    private BigDecimal getMonthlyPayment(BigDecimal loanAmount, int numberOfMonths, BigDecimal interestRatePerMonth) {
        BigDecimal base = interestRatePerMonth.add(BigDecimal.ONE);
        BigDecimal numerator = loanAmount.multiply(interestRatePerMonth).multiply(base.pow(numberOfMonths));
        BigDecimal denominator = base.pow(numberOfMonths).subtract(BigDecimal.ONE);
        return ServiceUtils.roundNumbers(numerator.divide(denominator, RoundingMode.HALF_UP), 2);
    }

    private BigDecimal getTotalInterestPaid(LoanRequest loanRequest, int numberOfMonths, BigDecimal monthlyPayment) {
        BigDecimal interest = (monthlyPayment.multiply(new BigDecimal(String.valueOf(numberOfMonths)))).subtract(loanRequest.getLoanAmount());
        return ServiceUtils.roundNumbers(interest, 2);
    }

    private int getNumberOfMonths(LoanTerm loanTerm) {
        return (loanTerm.getDurationPeriod() == DurationPeriod.MONTHS) ? loanTerm.getDuration() : loanTerm.getDuration() * 12;
    }
}
