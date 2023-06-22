package com.leanpay.loancalculator.service;

import com.leanpay.loancalculator.model.enums.DurationPeriod;
import com.leanpay.loancalculator.model.request.loan.LoanRequest;
import com.leanpay.loancalculator.model.request.loan.LoanTerm;
import com.leanpay.loancalculator.model.response.loan.LoanResponse;
import com.leanpay.loancalculator.repository.LoanCalculationRepository;
import com.leanpay.loancalculator.repository.LoanCalculationResponseRepository;
import com.leanpay.loancalculator.utils.ServiceUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LoanCalculationService {

    private final LoanCalculationRepository loanCalculationRepository;
    private final LoanCalculationResponseRepository loanCalculationResponseRepository;
    public LoanResponse calculateLoanPayments(LoanRequest loanRequest) {
        int numberOfPayments = getNumberOfMonths(loanRequest.getLoanTerm());

        double interestRatePerMonth = calculateMonthlyInterestRate(loanRequest.getInterestRate());

        double monthlyPayment = getMonthlyPayment(loanRequest.getLoanAmount(), numberOfPayments, interestRatePerMonth);

        double totalInterestPaid = getTotalInterestPaid(loanRequest, numberOfPayments, monthlyPayment);

        LoanResponse response = buildLoanResponse(monthlyPayment, totalInterestPaid);

        loanCalculationRepository.save(loanRequest);
        loanCalculationResponseRepository.save(response);

        return response;
    }

    private double calculateMonthlyInterestRate(double interestRate) {
        double monthlyInterestRate =  interestRate / 100 / DurationPeriod.MONTHS.getDurationPerYear();
        return ServiceUtils.roundNumbers(monthlyInterestRate, 6);
    }

    private double getMonthlyPayment(double loanAmount, int numberOfMonths, double interestRatePerMonth) {
        double numerator = loanAmount * interestRatePerMonth * Math.pow((1 + interestRatePerMonth), numberOfMonths);
        double denominator = Math.pow((1 + interestRatePerMonth), numberOfMonths) - 1;
        return ServiceUtils.roundNumbers(numerator/denominator,2);
    }

    private double getTotalInterestPaid(LoanRequest loanRequest, int numberOfMonths, double monthlyPayment) {
        double interest =  (monthlyPayment * numberOfMonths) - loanRequest.getLoanAmount();
        return ServiceUtils.roundNumbers(interest, 2);
    }

    private LoanResponse buildLoanResponse(double monthlyPayment, double totalInterestPaid) {
        return LoanResponse.builder()
                .monthlyPayment(monthlyPayment)
                .totalInterestPaid(totalInterestPaid)
                .build();
    }

    private int getNumberOfMonths(LoanTerm loanTerm) {
        return (loanTerm.getDurationPeriod() == DurationPeriod.MONTHS) ? loanTerm.getDuration() : loanTerm.getDuration() * 12;
    }
}
