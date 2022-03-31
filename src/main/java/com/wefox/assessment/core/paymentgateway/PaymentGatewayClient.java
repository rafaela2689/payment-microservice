package com.wefox.assessment.core.paymentgateway;

import com.wefox.assessment.core.payment.dto.PaymentDTO;
import com.wefox.assessment.core.paymentgateway.exceptions.UnableToValidatePayment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class PaymentGatewayClient {

    private final Logger LOG = LoggerFactory.getLogger(PaymentGatewayClient.class);

    private final String baseUrl;

    private final RestTemplate restTemplateHttpClient;

    @Autowired
    public PaymentGatewayClient(@Value("${base.url}") final String baseUrl,
                                final RestTemplate restTemplateHttpClient) {
        this.baseUrl = baseUrl;
        this.restTemplateHttpClient = restTemplateHttpClient;
    }

    public ResponseEntity<String> validatePayment(final PaymentDTO paymentDTO) {
        try {
            HttpEntity<PaymentDTO> request = new HttpEntity<>(paymentDTO, defaultHeaders());

            LOG.info("Validating payment request: " + request.getBody());
            final ResponseEntity<String> responseEntity = restTemplateHttpClient
                    .exchange(baseUrl + "/payment", HttpMethod.POST, request, String.class);

            LOG.info("Payment validation returned {}", responseEntity.getStatusCode());

            this.throwsIfNot2xx(responseEntity);

            return responseEntity;
        } catch (Exception ex) {
            throw new UnableToValidatePayment(paymentDTO, ex);
        }
    }

    public HttpHeaders defaultHeaders() {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }

    private void throwsIfNot2xx(final ResponseEntity<String> responseEntity) {
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException(String
                    .format("Not 2xx status code for operation, it was %d with response %s",
                            responseEntity.getStatusCode().value(), responseEntity));
        }
    }

}

