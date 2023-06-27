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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
        AmortizationRequest amortizationRequest = ServiceTestUtils.buildAmortizationRequest(10000,5,3,PaymentFrequency.MONTHLY);
        AmortizationSchedule firstRate = ServiceUtils.buildAmortizationSchedule(1,3361.15,3319.48,41.67,6680.52);
        AmortizationSchedule secondRate = ServiceUtils.buildAmortizationSchedule(2,3361.15,3333.31,27.84,3347.21);
        AmortizationSchedule thirdRate = ServiceUtils.buildAmortizationSchedule(3,3361.15,3347.2,13.95,0.01);
        AmortizationResponse expectedResponse = ServiceUtils.buildAmortizationResponse(10000, List.of(firstRate,secondRate,thirdRate));

        when(amortizationCalculationResultRepository.save(expectedResponse)).thenReturn(expectedResponse);

        // When
        AmortizationResponse actualResponse = amortizationCalculationService.calculateAmortizationObligations(amortizationRequest);

        // Then
        assertEquals(expectedResponse, actualResponse);
        verify(amortizationCalculationRepository, times(1)).save(amortizationRequest);
        verify(amortizationCalculationResultRepository, times(1)).save(expectedResponse);
    }

    @Test
    public void testListAllAmortizations() {
        // Given
        AmortizationSchedule firstMonthlyRate = ServiceUtils.buildAmortizationSchedule(1,3361.15,3319.48,41.67,6680.52);
        AmortizationSchedule secondMonthlyRate = ServiceUtils.buildAmortizationSchedule(2,3361.15,3333.31,27.84,3347.21);
        AmortizationSchedule thirdMonthlyRate = ServiceUtils.buildAmortizationSchedule(3,3361.15,3347.2,13.95,0.01);

        AmortizationSchedule firstDailyRate = ServiceUtils.buildAmortizationSchedule(1,10756.1,9756.1,1000,10243.9);
        AmortizationSchedule secondDailyRate = ServiceUtils.buildAmortizationSchedule(2,10756.1,10243.9,512.2,0);

        AmortizationResponse firstAmortizationResponse = ServiceUtils.buildAmortizationResponse(10000,List.of(firstMonthlyRate, secondMonthlyRate, thirdMonthlyRate));
        AmortizationResponse secondAmortizationResponse = ServiceUtils.buildAmortizationResponse(20000,List.of(firstDailyRate, secondDailyRate));

        List<AmortizationResponse> expectedResponse = List.of(firstAmortizationResponse, secondAmortizationResponse);

        when(amortizationCalculationResultRepository.findAll()).thenReturn(expectedResponse);

        // When
        List<AmortizationResponse> actualResponse = amortizationCalculationService.listAllAmortizations();

        // Then
        assertEquals(actualResponse,expectedResponse);
        verify(amortizationCalculationResultRepository, times(1)).findAll();
    }

    @Test
    public void testCalculateAmortizationObligations_WhenAmortizationRequestIsNull() {
        // Given
        AmortizationRequest amortizationRequest = null;
        AmortizationResponse expectedResponse = AmortizationResponse.builder().build();

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
        List<AmortizationResponse> actualResponse = amortizationCalculationService.listAllAmortizations();

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