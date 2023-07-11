package com.leanpay.loancalculator.model.request.loan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.DecimalMin;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document
@Data
@Builder
public class LoanRequest {
    @Id
    @JsonIgnore
    private String id;
    @DecimalMin(value = "10000.00")
    private BigDecimal loanAmount;
    @DecimalMin(value = "5.00")
    private BigDecimal interestRate;
    private LoanTerm loanTerm;
}
