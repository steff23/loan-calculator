package com.leanpay.loancalculator.controller;

import com.leanpay.loancalculator.model.request.amortization.AmortizationRequest;
import com.leanpay.loancalculator.model.request.loan.LoanRequest;
import com.leanpay.loancalculator.model.response.amortization.AmortizationResponse;
import com.leanpay.loancalculator.model.response.loan.LoanResponse;
import com.leanpay.loancalculator.service.AmortizationCalculationService;
import com.leanpay.loancalculator.service.LoanCalculationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/calculator")
public class LoanCalculatorController {

    private final LoanCalculationService loanCalculationService;
    private final AmortizationCalculationService amortizationCalculationService;

    @PostMapping("/loan")
    public ResponseEntity<LoanResponse> calculateLoan(@Valid @RequestBody LoanRequest loanRequest) throws MethodArgumentNotValidException {
        return loanCalculationService.calculateLoanPayments(loanRequest);
    }

    @PostMapping("/amortization")
    public ResponseEntity<AmortizationResponse> calculateAmortization(@Valid @RequestBody AmortizationRequest amortizationRequest) throws MethodArgumentNotValidException {
        return amortizationCalculationService.calculateAmortizationObligations(amortizationRequest);
    }

    @GetMapping("/loans")
    public ResponseEntity<List<LoanResponse>> listLoans() {
        return loanCalculationService.listAllLoans();
    }

    @GetMapping("/amortizations")
    public ResponseEntity<List<AmortizationResponse>> listAmortizations() {
        return amortizationCalculationService.listAllAmortizations();
    }
}
