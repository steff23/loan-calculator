package com.leanpay.loancalculator.integration;

import com.leanpay.loancalculator.model.response.amortization.AmortizationResponse;
import com.leanpay.loancalculator.model.response.loan.LoanResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoanCalculatorIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private HttpHeaders headers;

    @BeforeEach
    public void setUp() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    public void testCalculateLoan() {
        // Given
        String requestBody = "{ \"loanAmount\": 10000, \"interestRate\": 5, \"loanTerm\": { \"duration\": 5, \"durationPeriod\": \"YEARS\" } }";
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        // When
        ResponseEntity<LoanResponse> responseEntity = restTemplate.exchange(
                createURL("/calculator/loan"), HttpMethod.POST, requestEntity, LoanResponse.class);
        LoanResponse response = responseEntity.getBody();

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(response);
        assertEquals(188.71, response.getMonthlyPayment());
        assertEquals(1322.6, response.getTotalInterestPaid());
    }

    @Test
    public void testCalculateAmortization() {
        // Given
        String requestBody = "{ \"loanAmount\": 10000, \"interestRate\": 5, \"numberOfPayments\": 3, \"paymentFrequency\": \"MONTHLY\" }";
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        // When
        ResponseEntity<AmortizationResponse> responseEntity = restTemplate.exchange(
                createURL("/calculator/amortization"), HttpMethod.POST, requestEntity, AmortizationResponse.class);
        AmortizationResponse response = responseEntity.getBody();

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(response);
        assertEquals(3361.15, response.getAmortizationSchedule().get(0).getPaymentAmount());
        assertEquals(3319.48, response.getAmortizationSchedule().get(0).getPrincipalAmount());
        assertEquals(41.67, response.getAmortizationSchedule().get(0).getInterestAmount());
        assertEquals(6680.52, response.getAmortizationSchedule().get(0).getBalanceOwed());
        assertEquals(3361.15, response.getAmortizationSchedule().get(1).getPaymentAmount());
        assertEquals(3333.31, response.getAmortizationSchedule().get(1).getPrincipalAmount());
        assertEquals(27.84, response.getAmortizationSchedule().get(1).getInterestAmount());
        assertEquals(3347.21, response.getAmortizationSchedule().get(1).getBalanceOwed());
        assertEquals(3361.15, response.getAmortizationSchedule().get(2).getPaymentAmount());
        assertEquals(3347.2, response.getAmortizationSchedule().get(2).getPrincipalAmount());
        assertEquals(13.95, response.getAmortizationSchedule().get(2).getInterestAmount());
        assertEquals(0.01, response.getAmortizationSchedule().get(2).getBalanceOwed());
    }

    @Test
    public void testListLoans() {
        // When
        ResponseEntity<LoanResponse[]> responseEntity = restTemplate.exchange(
                createURL("/calculator/loans"), HttpMethod.GET, null, LoanResponse[].class);
        LoanResponse[] response = responseEntity.getBody();

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(response);
    }

    @Test
    public void testListAmortizations() {
        // When
        ResponseEntity<AmortizationResponse[]> responseEntity = restTemplate.exchange(
                createURL("/calculator/amortizations"), HttpMethod.GET, null, AmortizationResponse[].class);
        AmortizationResponse[] response = responseEntity.getBody();

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(response);
    }

    private String createURL(String uri) {
        return "http://localhost:" + port + uri;
    }
}
