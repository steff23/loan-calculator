package com.leanpay.loancalculator.repository;

import com.leanpay.loancalculator.model.request.loan.LoanRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanCalculationRepository extends MongoRepository<LoanRequest, String> {
}
