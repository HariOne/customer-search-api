package com.company.customersearch.client;

import com.company.customersearch.exception.ExternalApiException;
import com.company.customersearch.model.Customer;
import com.company.customersearch.util.CorrelationIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ThirdPartyCustomerClient {

    private final WebClient webClient;
    private final String apiUrl;
    private final long timeoutSeconds;
    private final int maxRetries;
    private final long retryDelayMillis;

    public ThirdPartyCustomerClient(
            WebClient webClient,
            @Value("${third-party-api.url}") String apiUrl,
            @Value("${third-party-api.timeout-seconds:30}") long timeoutSeconds,
            @Value("${third-party-api.max-retries:3}") int maxRetries,
            @Value("${third-party-api.retry-delay-millis:1000}") long retryDelayMillis) {
        this.webClient = webClient;
        this.apiUrl = apiUrl;
        this.timeoutSeconds = timeoutSeconds;
        this.maxRetries = maxRetries;
        this.retryDelayMillis = retryDelayMillis;
    }

    public List<Customer> searchCustomers(Map<String, String> queryParams) {
        String correlationId = CorrelationIdUtil.getCorrelationId();
        log.info("Searching for customers with params: {}. Correlation ID: {}", queryParams, correlationId);

        try {
            List<Customer> response = webClient.get()
                    .uri(uriBuilder -> {
                        uriBuilder.path(apiUrl);
                        if (queryParams != null) {
                            queryParams.forEach((key, value) -> {
                                if (value != null && !value.trim().isEmpty()) {
                                    uriBuilder.queryParam(key, value);
                                }
                            });
                        }
                        return uriBuilder.build();
                    })
                    .header("Correlation-Id", correlationId)
                    .retrieve()
                    .onStatus(
                            status -> !status.is2xxSuccessful(),
                            clientResponse -> Mono.error(
                                    new ExternalApiException(
                                            "External API returned error. Status: " + clientResponse.getStatusCode(),
                                            clientResponse.getStatusCode().value())))
                    .bodyToFlux(Customer.class)
                    .collectList()
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .retryWhen(Retry.max(maxRetries)
                            .delayElements(Duration.ofMillis(retryDelayMillis))
                            .onRetryExhaustedThrow((retrySignal, throwable) ->
                                    new ExternalApiException(
                                            "Max retries exceeded for external API call",
                                            throwable)))
                    .doOnError(error -> log.error(
                            "Error calling external API. Correlation ID: {}. Error: {}",
                            correlationId, error.getMessage(), error))
                    .block();

            log.info("External API returned {} customer records. Correlation ID: {}",
                    response != null ? response.size() : 0, correlationId);
            return response != null ? response : List.of();

        } catch (Exception e) {
            log.error("Failed to call external API. Correlation ID: {}", correlationId, e);
            throw new ExternalApiException("Failed to fetch customers from external API: " + e.getMessage(), e);
        }
    }
}
