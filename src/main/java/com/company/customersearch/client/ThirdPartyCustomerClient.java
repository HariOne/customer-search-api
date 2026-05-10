package com.company.customersearch.client;

import com.company.customersearch.enums.Brand;
import com.company.customersearch.exception.ExternalApiException;
import com.company.customersearch.model.Customer;
import com.company.customersearch.util.CorrelationIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ThirdPartyCustomerClient {

    private final WebClient webClient;
    private final String externalApiBaseUrl;

    public ThirdPartyCustomerClient(WebClient webClient,
                                     @Value("${external.api.base-url:http://localhost:3000}") String externalApiBaseUrl) {
        this.webClient = webClient;
        this.externalApiBaseUrl = externalApiBaseUrl;
    }

    public List<Customer> searchCustomers(Brand brand, Map<String, String> filters) {
        String correlationId = CorrelationIdUtil.getCorrelationId();
        String url = externalApiBaseUrl + "/api/v1/" + brand.name().toLowerCase() + "/customers";

        try {
            log.debug("Calling external API: {} with filters: {}. Correlation ID: {}", url, filters, correlationId);

            List<Customer> customers = webClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/api/v1/{brand}/customers")
                                .queryParam("brand", brand.name().toLowerCase());

                        for (Map.Entry<String, String> entry : filters.entrySet()) {
                            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                                builder.queryParam(entry.getKey(), entry.getValue());
                            }
                        }

                        return builder.build("customers");
                    })
                    .header("Correlation-Id", correlationId)
                    .header("Content-Type", "application/json")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Customer>>() {})
                    .retryWhen(Retry.backoff(3, Duration.ofMillis(100))
                            .maxBackoff(Duration.ofSeconds(5))
                            .jitter(0.1))
                    .timeout(Duration.ofSeconds(30))
                    .block();

            log.debug("Successfully retrieved {} customers from external API. Correlation ID: {}", 
                    customers != null ? customers.size() : 0, correlationId);
            return customers;

        } catch (WebClientResponseException e) {
            log.error("External API returned error. Status: {}. Correlation ID: {}", e.getStatusCode(), correlationId, e);
            throw new ExternalApiException(
                    String.format("External API error: %s", e.getStatusText()),
                    e.getStatusCode().value());
        } catch (Exception e) {
            log.error("Error calling external API. Correlation ID: {}", correlationId, e);
            throw new ExternalApiException("Failed to call external API: " + e.getMessage(), e);
        }
    }
}
