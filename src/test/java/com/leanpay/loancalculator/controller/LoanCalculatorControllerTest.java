package com.leanpay.loancalculator.controller;

import com.leanpay.loancalculator.model.enums.DurationPeriod;
import com.leanpay.loancalculator.model.enums.PaymentFrequency;
import com.leanpay.loancalculator.model.request.amortization.AmortizationRequest;
import com.leanpay.loancalculator.model.request.loan.LoanRequest;
import com.leanpay.loancalculator.model.request.loan.LoanTerm;
import com.leanpay.loancalculator.model.response.amortization.AmortizationResponse;
import com.leanpay.loancalculator.model.response.amortization.AmortizationSchedule;
import com.leanpay.loancalculator.model.response.loan.LoanResponse;
import com.leanpay.loancalculator.service.AmortizationCalculationService;
import com.leanpay.loancalculator.service.LoanCalculationService;
import com.leanpay.loancalculator.utils.ServiceTestUtils;
import com.leanpay.loancalculator.utils.ServiceUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanCalculatorControllerTest {

    @Mock
    LoanCalculationService loanCalculationService;
    @Mock
    AmortizationCalculationService amortizationCalculationService;

    @InjectMocks
    LoanCalculatorController loanCalculatorController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        loanCalculatorController = new LoanCalculatorController(loanCalculationService, amortizationCalculationService);
    }

    @Test
    public void testCalculateLoan() {
        // Given
        LoanTerm loanTerm = ServiceTestUtils.buildLoanTerm(5, DurationPeriod.YEARS);
        LoanRequest loanRequest = ServiceTestUtils.buildLoanRequest(10000,5, loanTerm);
        LoanResponse expectedResponse = ServiceUtils.buildLoanResponse(188.71,1322.6);

        when(loanCalculationService.calculateLoanPayments(loanRequest)).thenReturn(expectedResponse);

        // When
        LoanResponse actualResponse = loanCalculatorController.calculateLoan(loanRequest);

        // Then
        assertEquals(expectedResponse, actualResponse);
        verify(loanCalculationService, times(1)).calculateLoanPayments(loanRequest);
    }

    @Test
    public void testListLoans() {
        // Given
        LoanResponse firstLoanResponse = ServiceUtils.buildLoanResponse(188.71,1322.6);
        LoanResponse secondLoanResponse = ServiceUtils.buildLoanResponse(895.45,1490.8);
        List<LoanResponse> expectedResponse = Arrays.asList(firstLoanResponse, secondLoanResponse);

        when(loanCalculationService.listAllLoans()).thenReturn(expectedResponse);

        // When
        List<LoanResponse> actualResponse = loanCalculatorController.listLoans();

        // Then
        assertEquals(expectedResponse, actualResponse);
        verify(loanCalculationService, times(1)).listAllLoans();
    }

    @Test
    public void testCalculateAmortization() {
        // Given
        AmortizationRequest amortizationRequest = ServiceTestUtils.buildAmortizationRequest(10000,5,3,PaymentFrequency.MONTHLY);
        AmortizationSchedule firstRate = ServiceUtils.buildAmortizationSchedule(1,3361.15,3319.48,41.67,6680.52);
        AmortizationSchedule secondRate = ServiceUtils.buildAmortizationSchedule(2,3361.15,3333.31,27.84,3347.21);
        AmortizationSchedule thirdRate = ServiceUtils.buildAmortizationSchedule(3,3361.15,3347.2,13.95,0.01);
        AmortizationResponse expectedResponse = ServiceUtils.buildAmortizationResponse(10000, List.of(firstRate,secondRate,thirdRate));

        when(amortizationCalculationService.calculateAmortizationObligations(amortizationRequest)).thenReturn(expectedResponse);

        // When
        AmortizationResponse actualResponse = loanCalculatorController.calculateAmortization(amortizationRequest);

        // Then
        assertEquals(expectedResponse, actualResponse);
        verify(amortizationCalculationService, times(1)).calculateAmortizationObligations(amortizationRequest);
    }

    @Test
    public void testListAmortizations() {
        // Given
        AmortizationSchedule firstMonthlyRate = ServiceUtils.buildAmortizationSchedule(1,3361.15,3319.48,41.67,6680.52);
        AmortizationSchedule secondMonthlyRate = ServiceUtils.buildAmortizationSchedule(2,3361.15,3333.31,27.84,3347.21);
        AmortizationSchedule thirdMonthlyRate = ServiceUtils.buildAmortizationSchedule(3,3361.15,3347.2,13.95,0.01);

        AmortizationSchedule firstDailyRate = ServiceUtils.buildAmortizationSchedule(1,10756.1,9756.1,1000,10243.9);
        AmortizationSchedule secondDailyRate = ServiceUtils.buildAmortizationSchedule(2,10756.1,10243.9,512.2,0);

        AmortizationResponse firstAmortizationResponse = ServiceUtils.buildAmortizationResponse(10000,List.of(firstMonthlyRate, secondMonthlyRate, thirdMonthlyRate));
        AmortizationResponse secondAmortizationResponse = ServiceUtils.buildAmortizationResponse(20000,List.of(firstDailyRate, secondDailyRate));

        List<AmortizationResponse> expectedResponse = List.of(firstAmortizationResponse, secondAmortizationResponse);

        when(amortizationCalculationService.listAllAmortizations()).thenReturn(expectedResponse);

        // When
        List<AmortizationResponse> actualResponse = loanCalculatorController.listAmortizations();

        // Then
        assertEquals(expectedResponse, actualResponse);
        verify(amortizationCalculationService, times(1)).listAllAmortizations();
    }

    @Test
    public void testListLoans_WhenLoanServiceThrowsException() {
        // Given
        when(loanCalculationService.listAllLoans()).thenThrow(RuntimeException.class);

        // When
        assertThrows(RuntimeException.class, () -> {
            loanCalculatorController.listLoans();
        });

        // Then
        verify(loanCalculationService, times(1)).listAllLoans();
    }

    @Test
    public void testListAmortizations_WhenAmortizationServiceThrowsException() {
        // Given
        when(amortizationCalculationService.listAllAmortizations()).thenThrow(RuntimeException.class);

        // When
        assertThrows(RuntimeException.class, () -> {
            loanCalculatorController.listAmortizations();
        });

        // Then
        verify(amortizationCalculationService, times(1)).listAllAmortizations();
    }

}