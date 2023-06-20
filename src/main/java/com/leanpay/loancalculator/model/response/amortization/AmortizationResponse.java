package com.leanpay.loancalculator.model.response.amortization;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Data
@Builder
public class AmortizationResponse {
    private String id;
    private double loanAmount;
    private List<AmortizationSchedule> amortizationSchedule;
}
