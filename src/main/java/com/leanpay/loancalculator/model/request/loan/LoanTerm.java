package com.leanpay.loancalculator.model.request.loan;

import com.leanpay.loancalculator.model.enums.DurationPeriod;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoanTerm {
    @Min(1)
    private int duration;
    @NotBlank
    private DurationPeriod durationPeriod;
}
