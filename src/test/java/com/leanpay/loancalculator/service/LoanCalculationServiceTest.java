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
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Disabled
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
        LoanRequest loanRequest = ServiceTestUtils.buildLoanRequest(new BigDecimal("10000"), new BigDecimal("5"), loanTerm);
        LoanResponse expectedResponse = ServiceUtils.buildLoanResponse(new BigDecimal("188.71"), new BigDecimal("1322.6"));

        // When
        when(loanCalculationResponseRepository.save(any(LoanResponse.class))).thenReturn(expectedResponse);
        ResponseEntity<LoanResponse> actualResponseEntity = loanCalculationService.calculateLoanPayments(loanRequest);
        LoanResponse actualResponse = actualResponseEntity.getBody();

        // Then
        assertEquals(expectedResponse, actualResponse);
        verify(loanCalculationRepository, times(1)).save(loanRequest);
        verify(loanCalculationResponseRepository, times(1)).save(expectedResponse);
    }

    @Test
    public void testListAllLoans() {
        // Given
        LoanResponse firstLoanResponse = ServiceUtils.buildLoanResponse(new BigDecimal("188.71"), new BigDecimal("1322.6"));
        LoanResponse secondLoanResponse = ServiceUtils.buildLoanResponse(new BigDecimal("895.45"), new BigDecimal("1490.8"));
        List<LoanResponse> expectedResponse = Arrays.asList(firstLoanResponse, secondLoanResponse);

        when(loanCalculationResponseRepository.findAll()).thenReturn(expectedResponse);

        // When
        ResponseEntity<List<LoanResponse>> actualResponseEntity = loanCalculationService.listAllLoans();
        List<LoanResponse> actualResponse = actualResponseEntity.getBody();

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