package com.leanpay.loancalculator.service;

import com.leanpay.loancalculator.model.enums.PaymentFrequency;
import com.leanpay.loancalculator.model.request.amortization.AmortizationRequest;
import com.leanpay.loancalculator.model.response.amortization.AmortizationResponse;
import com.leanpay.loancalculator.model.response.amortization.AmortizationSchedule;
import com.leanpay.loancalculator.repository.AmortizationCalculationRepository;
import com.leanpay.loancalculator.repository.AmortizationCalculationResultRepository;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Disabled
public class AmortizationCalculationServiceTest {

    @Mock
    private AmortizationCalculationRepository amortizationCalculationRepository;

    @Mock
    private AmortizationCalculationResultRepository amortizationCalculationResultRepository;

    @InjectMocks
    private AmortizationCalculationService amortizationCalculationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        amortizationCalculationService = new AmortizationCalculationService(amortizationCalculationRepository, amortizationCalculationResultRepository);
    }

    @Test
    public void testCalculateAmortizationObligations() {
        // Given
        AmortizationRequest amortizationRequest = ServiceTestUtils.buildAmortizationRequest(new BigDecimal("10000"), new BigDecimal("5"), 3, PaymentFrequency.MONTHLY);
        AmortizationSchedule firstRate = ServiceUtils.buildAmortizationSchedule(1, new BigDecimal("3361.15"), new BigDecimal("3319.48"), new BigDecimal("41.67"), new BigDecimal("6680.52"));
        AmortizationSchedule secondRate = ServiceUtils.buildAmortizationSchedule(2, new BigDecimal("3361.15"), new BigDecimal("3333.31"), new BigDecimal("27.84"), new BigDecimal("3347.21"));
        AmortizationSchedule thirdRate = ServiceUtils.buildAmortizationSchedule(3, new BigDecimal("3361.15"), new BigDecimal("3347.2"), new BigDecimal("13.95"), new BigDecimal("0.01"));
        AmortizationResponse expectedResponse = ServiceUtils.buildAmortizationResponse(new BigDecimal("10000"), List.of(firstRate, secondRate, thirdRate));

        when(amortizationCalculationResultRepository.save(expectedResponse)).thenReturn(expectedResponse);

        // When
        ResponseEntity<AmortizationResponse> actualResponseEntity = amortizationCalculationService.calculateAmortizationObligations(amortizationRequest);
        AmortizationResponse actualResponse = actualResponseEntity.getBody();

        // Then
        assertEquals(expectedResponse, actualResponse);
        verify(amortizationCalculationRepository, times(1)).save(amortizationRequest);
        verify(amortizationCalculationResultRepository, times(1)).save(expectedResponse);
    }

    @Test
    public void testListAllAmortizations() {
        // Given
        AmortizationSchedule firstMonthlyRate = ServiceUtils.buildAmortizationSchedule(1, new BigDecimal("3361.15"), new BigDecimal("3319.48"), new BigDecimal("41.67"), new BigDecimal("6680.52"));
        AmortizationSchedule secondMonthlyRate = ServiceUtils.buildAmortizationSchedule(2, new BigDecimal("3361.15"), new BigDecimal("3333.31"), new BigDecimal("27.84"), new BigDecimal("3347.21"));
        AmortizationSchedule thirdMonthlyRate = ServiceUtils.buildAmortizationSchedule(3, new BigDecimal("3361.15"), new BigDecimal("3347.2"), new BigDecimal("13.95"), new BigDecimal("0.01"));

        AmortizationSchedule firstDailyRate = ServiceUtils.buildAmortizationSchedule(1, new BigDecimal("10756.1"), new BigDecimal("9756.1"), new BigDecimal("1000"), new BigDecimal("10243.9"));
        AmortizationSchedule secondDailyRate = ServiceUtils.buildAmortizationSchedule(2, new BigDecimal("10756.1"), new BigDecimal("10243.9"), new BigDecimal("512.2"), new BigDecimal("0"));

        AmortizationResponse firstAmortizationResponse = ServiceUtils.buildAmortizationResponse(new BigDecimal("10000"), List.of(firstMonthlyRate, secondMonthlyRate, thirdMonthlyRate));
        AmortizationResponse secondAmortizationResponse = ServiceUtils.buildAmortizationResponse(new BigDecimal("20000"), List.of(firstDailyRate, secondDailyRate));

        List<AmortizationResponse> expectedResponse = List.of(firstAmortizationResponse, secondAmortizationResponse);

        when(amortizationCalculationResultRepository.findAll()).thenReturn(expectedResponse);

        // When
        ResponseEntity<List<AmortizationResponse>> actualResponseEntity = amortizationCalculationService.listAllAmortizations();
        List<AmortizationResponse> actualResponse = actualResponseEntity.getBody();

        // Then
        assertEquals(actualResponse, expectedResponse);
        verify(amortizationCalculationResultRepository, times(1)).findAll();
    }

    @Test
    public void testCalculateAmortizationObligations_WhenAmortizationRequestIsNull() {
        // When
        assertThrows(NullPointerException.class, () -> {
            amortizationCalculationService.calculateAmortizationObligations(null);
        });

        // Then
        verify(amortizationCalculationRepository, never()).save(any());
        verify(amortizationCalculationResultRepository, never()).save(any());
    }

    @Test
    public void testListAllAmortizations_WhenNoAmortizationResponsesExist() {
        // Given
        when(amortizationCalculationResultRepository.findAll()).thenReturn(null);

        // When
        ResponseEntity<List<AmortizationResponse>> actualResponseEntity = amortizationCalculationService.listAllAmortizations();
        List<AmortizationResponse> actualResponse = actualResponseEntity.getBody();

        // Then
        assertNull(actualResponse);
        verify(amortizationCalculationResultRepository, times(1)).findAll();
    }

    @Test
    public void testListAllAmortizations_WhenAmortizationRepositoryThrowsException() {
        // Given
        when(amortizationCalculationResultRepository.findAll()).thenThrow(RuntimeException.class);

        // When
        assertThrows(RuntimeException.class, () -> {
            amortizationCalculationService.listAllAmortizations();
        });

        // Then
        verify(amortizationCalculationResultRepository, times(1)).findAll();
    }
}