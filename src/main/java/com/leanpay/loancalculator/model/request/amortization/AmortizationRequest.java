package com.leanpay.loancalculator.model.request.amortization;

import com.leanpay.loancalculator.model.enums.PaymentFrequency;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@Builder
public class AmortizationRequest {
    private String id;
    private double loanAmount;
    private double interestRate;
    private int numberOfPayments;
    private PaymentFrequency paymentFrequency;
}
