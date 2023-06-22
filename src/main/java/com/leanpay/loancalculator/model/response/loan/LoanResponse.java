package com.leanpay.loancalculator.model.response.loan;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@Builder
public class LoanResponse {
    @Id
    private String id;
    private double monthlyPayment;
    private double totalInterestPaid;
}
