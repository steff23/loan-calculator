package com.leanpay.loancalculator.model.request.loan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@Builder
public class LoanRequest {
    @Id
    @JsonIgnore
    private String id;
    private double loanAmount;
    private double interestRate;
    private LoanTerm loanTerm;
}
