package com.leanpay.loancalculator.repository;

import com.leanpay.loancalculator.model.response.amortization.AmortizationResponse;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AmortizationCalculationResultRepository extends MongoRepository<AmortizationResponse, String> {
}
