package com.leanpay.loancalculator.repository;

import com.leanpay.loancalculator.model.request.amortization.AmortizationRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AmortizationCalculationRepository extends MongoRepository<AmortizationRequest, String> {
}
