package com.leanpay.loancalculator.model.request.loan;

import com.leanpay.loancalculator.model.enums.DurationPeriod;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoanTerm {
    private int duration;
    private DurationPeriod durationPeriod;
}
