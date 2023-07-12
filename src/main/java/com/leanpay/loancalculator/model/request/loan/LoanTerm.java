package com.leanpay.loancalculator.model.request.loan;

import com.leanpay.loancalculator.model.enums.DurationPeriod;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoanTerm {
    @Positive
    private int duration;
    private DurationPeriod durationPeriod;
}
