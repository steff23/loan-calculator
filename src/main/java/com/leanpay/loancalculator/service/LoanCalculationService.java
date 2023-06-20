package com.leanpay.loancalculator.service;

import com.leanpay.loancalculator.model.enums.DurationPeriod;
import com.leanpay.loancalculator.model.request.loan.LoanRequest;
import com.leanpay.loancalculator.model.request.loan.LoanTerm;
import com.leanpay.loancalculator.model.response.loan.LoanResponse;
import com.leanpay.loancalculator.utils.ServiceUtils;
import org.springframework.stereotype.Service;

@Service
public class LoanCalculationService {
    public LoanResponse calculateLoanPayments(LoanRequest loanRequest) {
        int numberOfPayments = getNumberOfMonths(loanRequest.getLoanTerm());

        double interestRatePerMonth = ServiceUtils.calculateMonthlyInterestRate(loanRequest.getInterestRate());
        double roundedInterestRate = ServiceUtils.roundNumbers(interestRatePerMonth, 6);

        double monthlyPayment = ServiceUtils.getMonthlyPayment(loanRequest.getLoanAmount(), numberOfPayments, roundedInterestRate);
        double roundedMonthlyPayment = ServiceUtils.roundNumbers(monthlyPayment,2);

        double totalInterestPaid = getTotalInterestPaid(loanRequest, numberOfPayments, roundedMonthlyPayment);
        return buildLoanResponse(roundedMonthlyPayment, totalInterestPaid);
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
