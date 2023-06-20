package com.leanpay.loancalculator.controller;

import com.leanpay.loancalculator.model.request.amortization.AmortizationRequest;
import com.leanpay.loancalculator.model.request.loan.LoanRequest;
import com.leanpay.loancalculator.model.response.amortization.AmortizationResponse;
import com.leanpay.loancalculator.model.response.loan.LoanResponse;
import com.leanpay.loancalculator.service.AmortizationCalculationService;
import com.leanpay.loancalculator.service.LoanCalculationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/calculator")
public class LoanCalculatorController {

    private final LoanCalculationService loanCalculationService;
    private final AmortizationCalculationService amortizationCalculationService;

    @PostMapping("/loan")
    @CrossOrigin
    private LoanResponse calculateLoan(@RequestBody LoanRequest loanRequest) {
        return loanCalculationService.calculateLoanPayments(loanRequest);
    }

    @PostMapping("/amortization")
    @CrossOrigin
    private AmortizationResponse calculateAmortization(@RequestBody AmortizationRequest amortizationRequest) {
        return amortizationCalculationService.calculateAmortizationObligations(amortizationRequest);
    }
}
