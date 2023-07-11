package com.leanpay.loancalculator.model.request.amortization;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.leanpay.loancalculator.model.enums.PaymentFrequency;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document
@Data
@Builder
public class AmortizationRequest {
    @Id
    @JsonIgnore
    private String id;
    @DecimalMin(value = "10000.00")
    private BigDecimal loanAmount;
    @DecimalMin(value = "5.00")
    private BigDecimal interestRate;
    @DecimalMin(value = "1.00")
    private int numberOfPayments;
    @NotBlank
    private PaymentFrequency paymentFrequency;
}
