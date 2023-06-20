package com.leanpay.loancalculator.model.request.loan;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@Builder
public class LoanRequest {
    private String id;
    private double loanAmount;
    private double interestRate;
    private LoanTerm loanTerm;
}