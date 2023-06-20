package com.leanpay.loancalculator.model.response.amortization;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AmortizationSchedule {
    private int id;
    private double paymentAmount;
    private double principalAmount;
    private double interestAmount;
    private double balanceOwed;
}
