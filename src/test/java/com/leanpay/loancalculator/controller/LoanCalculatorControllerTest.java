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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Disabled
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
    public void testCalculateLoan() throws MethodArgumentNotValidException {
        // Given
        LoanTerm loanTerm = ServiceTestUtils.buildLoanTerm(5, DurationPeriod.YEARS);
        LoanRequest loanRequest = ServiceTestUtils.buildLoanRequest(new BigDecimal("10000"), new BigDecimal("5"), loanTerm);
        LoanResponse expectedResponse = ServiceUtils.buildLoanResponse(new BigDecimal("188.71"), new BigDecimal("1322.6"));
        ResponseEntity<LoanResponse> expectedResponseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.CREATED);

        when(loanCalculationService.calculateLoanPayments(loanRequest)).thenReturn(expectedResponseEntity);

        // When
        ResponseEntity<LoanResponse> actualResponseEntity = loanCalculatorController.calculateLoan(loanRequest);
        LoanResponse actualResponse = actualResponseEntity.getBody();

        // Then
        assertEquals(expectedResponse, actualResponse);
        verify(loanCalculationService, times(1)).calculateLoanPayments(loanRequest);
    }

    @Test
    public void testListLoans() {
        // Given
        LoanResponse firstLoanResponse = ServiceUtils.buildLoanResponse(new BigDecimal("188.71"), new BigDecimal("1322.6"));
        LoanResponse secondLoanResponse = ServiceUtils.buildLoanResponse(new BigDecimal("895.45"), new BigDecimal("1490.8"));
        List<LoanResponse> expectedResponse = Arrays.asList(firstLoanResponse, secondLoanResponse);
        ResponseEntity<List<LoanResponse>> expectedResponseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        when(loanCalculationService.listAllLoans()).thenReturn(expectedResponseEntity);

        // When
        ResponseEntity<List<LoanResponse>> actualResponseEntity = loanCalculatorController.listLoans();
        List<LoanResponse> actualResponse = actualResponseEntity.getBody();

        // Then
        assertEquals(expectedResponse, actualResponse);
        verify(loanCalculationService, times(1)).listAllLoans();
    }

    @Test
    public void testCalculateAmortization() throws MethodArgumentNotValidException {
        // Given
        AmortizationRequest amortizationRequest = ServiceTestUtils.buildAmortizationRequest(new BigDecimal("10000"), new BigDecimal("5"), 3, PaymentFrequency.MONTHLY);
        AmortizationSchedule firstRate = ServiceUtils.buildAmortizationSchedule(1, new BigDecimal("3361.15"), new BigDecimal("3319.48"), new BigDecimal("41.67"), new BigDecimal("6680.52"));
        AmortizationSchedule secondRate = ServiceUtils.buildAmortizationSchedule(2, new BigDecimal("3361.15"), new BigDecimal("3333.31"), new BigDecimal("27.84"), new BigDecimal("3347.21"));
        AmortizationSchedule thirdRate = ServiceUtils.buildAmortizationSchedule(3, new BigDecimal("3361.15"), new BigDecimal("3347.2"), new BigDecimal("13.95"), new BigDecimal("0.01"));
        AmortizationResponse expectedResponse = ServiceUtils.buildAmortizationResponse(new BigDecimal("10000"), List.of(firstRate, secondRate, thirdRate));
        ResponseEntity<AmortizationResponse> expectedResponseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.CREATED);

        when(amortizationCalculationService.calculateAmortizationObligations(amortizationRequest)).thenReturn(expectedResponseEntity);

        // When
        ResponseEntity<AmortizationResponse> actualResponseEntity = loanCalculatorController.calculateAmortization(amortizationRequest);
        AmortizationResponse actualResponse = actualResponseEntity.getBody();

        // Then
        assertEquals(expectedResponse, actualResponse);
        verify(amortizationCalculationService, times(1)).calculateAmortizationObligations(amortizationRequest);
    }

    @Test
    public void testListAmortizations() {
        // Given
        AmortizationSchedule firstMonthlyRate = ServiceUtils.buildAmortizationSchedule(1, new BigDecimal("3361.15"), new BigDecimal("3319.48"), new BigDecimal("41.67"), new BigDecimal("6680.52"));
        AmortizationSchedule secondMonthlyRate = ServiceUtils.buildAmortizationSchedule(2, new BigDecimal("3361.15"), new BigDecimal("3333.31"), new BigDecimal("27.84"), new BigDecimal("3347.21"));
        AmortizationSchedule thirdMonthlyRate = ServiceUtils.buildAmortizationSchedule(3, new BigDecimal("3361.15"), new BigDecimal("3347.2"), new BigDecimal("13.95"), new BigDecimal("0.01"));

        AmortizationSchedule firstDailyRate = ServiceUtils.buildAmortizationSchedule(1, new BigDecimal("10756.1"), new BigDecimal("9756.1"), new BigDecimal("1000"), new BigDecimal("10243.9"));
        AmortizationSchedule secondDailyRate = ServiceUtils.buildAmortizationSchedule(2, new BigDecimal("10756.1"), new BigDecimal("10243.9"), new BigDecimal("512.2"), new BigDecimal("0"));

        AmortizationResponse firstAmortizationResponse = ServiceUtils.buildAmortizationResponse(new BigDecimal("10000"), List.of(firstMonthlyRate, secondMonthlyRate, thirdMonthlyRate));
        AmortizationResponse secondAmortizationResponse = ServiceUtils.buildAmortizationResponse(new BigDecimal("20000"), List.of(firstDailyRate, secondDailyRate));

        List<AmortizationResponse> expectedResponse = List.of(firstAmortizationResponse, secondAmortizationResponse);
        ResponseEntity<List<AmortizationResponse>> expectedResponseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        when(amortizationCalculationService.listAllAmortizations()).thenReturn(expectedResponseEntity);

        // When
        ResponseEntity<List<AmortizationResponse>> actualResponseEntity = loanCalculatorController.listAmortizations();
        List<AmortizationResponse> actualResponse = actualResponseEntity.getBody();

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