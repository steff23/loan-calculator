package com.leanpay.loancalculator.service;

import com.leanpay.loancalculator.model.enums.DurationPeriod;
import com.leanpay.loancalculator.model.request.loan.LoanRequest;
import com.leanpay.loancalculator.model.request.loan.LoanTerm;
import com.leanpay.loancalculator.model.response.loan.LoanResponse;
import com.leanpay.loancalculator.repository.LoanCalculationRepository;
import com.leanpay.loancalculator.repository.LoanCalculationResponseRepository;
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
class LoanCalculationServiceTest {
    @Mock
    private LoanCalculationRepository loanCalculationRepository;

    @Mock
    private LoanCalculationResponseRepository loanCalculationResponseRepository;

    @InjectMocks
    private LoanCalculationService loanCalculationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        loanCalculationService = new LoanCalculationService(loanCalculationRepository, loanCalculationResponseRepository);
    }

    @Test
    public void testCalculateLoanPayments() {
        // Given
        LoanTerm loanTerm = ServiceTestUtils.buildLoanTerm(5, DurationPeriod.YEARS);
        LoanRequest loanRequest = ServiceTestUtils.buildLoanRequest(10000,5, loanTerm);
        LoanResponse expectedResponse = ServiceUtils.buildLoanResponse(188.71,1322.6);

        // When
        when(loanCalculationResponseRepository.save(any(LoanResponse.class))).thenReturn(expectedResponse);
        LoanResponse actualResponse = loanCalculationService.calculateLoanPayments(loanRequest);

        // Then
        assertEquals(expectedResponse, actualResponse);
        verify(loanCalculationRepository, times(1)).save(loanRequest);
        verify(loanCalculationResponseRepository, times(1)).save(expectedResponse);
    }

    @Test
    public void testListAllLoans() {
        // Given
        LoanResponse firstLoanResponse = ServiceUtils.buildLoanResponse(188.71,1322.6);
        LoanResponse secondLoanResponse = ServiceUtils.buildLoanResponse(895.45,1490.8);
        List<LoanResponse> expectedResponse = Arrays.asList(firstLoanResponse, secondLoanResponse);

        when(loanCalculationResponseRepository.findAll()).thenReturn(expectedResponse);

        // When
        List<LoanResponse> actualResponse = loanCalculationService.listAllLoans();

        // Then
        assertEquals(expectedResponse, actualResponse);
        verify(loanCalculationResponseRepository, times(1)).findAll();
    }

    @Test
    public void testCalculateLoanPayments_WhenLoanRequestIsNull() {
        // When
        assertThrows(NullPointerException.class, () -> {
            loanCalculationService.calculateLoanPayments(null);
        });

        // Then
        verify(loanCalculationRepository, never()).save(any());
        verify(loanCalculationResponseRepository, never()).save(any());
    }

    @Test
    public void testListAllLoans_WhenLoanResponseRepositoryThrowsException() {
        // Given
        when(loanCalculationResponseRepository.findAll()).thenThrow(RuntimeException.class);

        // When
        assertThrows(RuntimeException.class, () -> {
            loanCalculationService.listAllLoans();
        });

        // Then
        verify(loanCalculationResponseRepository, times(1)).findAll();
    }

}