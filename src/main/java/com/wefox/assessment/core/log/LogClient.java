package com.wefox.assessment.core.log;

import com.wefox.assessment.core.log.dto.ErrorDTO;
import com.wefox.assessment.core.log.exceptions.UnableToSaveErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class LogClient {
    private final Logger LOG = LoggerFactory.getLogger(LogClient.class);

    private final String baseUrl;

    private final RestTemplate restTemplateHttpClient;

    @Autowired
    public LogClient(@Value("${base.url}") final String baseUrl,
                                 final RestTemplate restTemplateHttpClient) {
        this.baseUrl = baseUrl;
        this.restTemplateHttpClient = restTemplateHttpClient;
    }

    public ResponseEntity<String> saveError(final ErrorDTO errorDTO) {

        try {
            HttpEntity<ErrorDTO> request = new HttpEntity<>(errorDTO, defaultHeaders());

            LOG.info("Save log error request: " + request.getBody());
            final ResponseEntity<String> responseEntity = restTemplateHttpClient
                    .exchange(baseUrl + "/log", HttpMethod.POST, request, String.class);

            LOG.info("Saving error returned {}, body: {}",
                    responseEntity.getStatusCode(), responseEntity.getBody());

            this.throwsIfNot2xx(responseEntity);

            return responseEntity;
        } catch (Exception ex) {
            throw new UnableToSaveErrorException(errorDTO, ex);
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
