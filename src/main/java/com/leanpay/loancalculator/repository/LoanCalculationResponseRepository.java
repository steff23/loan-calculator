package com.leanpay.loancalculator.repository;

import com.leanpay.loancalculator.model.response.loan.LoanResponse;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LoanCalculationResponseRepository extends MongoRepository<LoanResponse, String> {
}
