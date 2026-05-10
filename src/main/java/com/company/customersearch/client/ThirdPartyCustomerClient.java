package com.company.customersearch.client;

import com.company.customersearch.exception.ExternalApiException;
import com.company.customersearch.model.Customer;
import com.company.customersearch.util.CorrelationIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ThirdPartyCustomerClient {

    private final WebClient webClient;

    @Value("${external-api.base-url}")
    private String baseUrl;

    @Value("${external-api.endpoint.customers}")
    private String customersEndpoint;

    @Value("${external-api.retry.max-attempts:3}")
    private int maxRetries;

    @Value("${external-api.retry.delay-ms:1000}")
    private long retryDelayMs;

    public ThirdPartyCustomerClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public List<Customer> searchCustomers(Map<String, String> queryParams) {
        String correlationId = CorrelationIdUtil.getCorrelationId();
        log.info("Searching customers from external API. Correlation ID: {}. Query params: {}",
                correlationId, queryParams);

        String url = buildUrl(queryParams);
        log.debug("Calling external API: {}. Correlation ID: {}", url, correlationId);

        return webClient.get()
                .uri(url)
                .header("Correlation-Id", correlationId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("External API error. Status: {}. Body: {}. Correlation ID: {}",
                                            response.getStatusCode(), errorBody, correlationId);
                                    return Mono.error(new ExternalApiException(
                                            String.format("External API error: %s", errorBody),
                                            response.getStatusCode().value()));
                                })
                )
                .bodyToFlux(Customer.class)
                .retryWhen(Retry.backoff(maxRetries, Duration.ofMillis(retryDelayMs))
                        .doBeforeRetry(signal -> log.warn("Retrying external API call. Attempt: {}. Correlation ID: {}",
                                signal.totalRetries() + 1, correlationId))
                )
                .collectList()
                .onErrorMap(throwable -> {
                    if (throwable instanceof ExternalApiException) {
                        return throwable;
                    }
                    log.error("Error calling external API. Correlation ID: {}", correlationId, throwable);
                    return new ExternalApiException(
                            String.format("Error calling external API: %s", throwable.getMessage()),
                            throwable);
                })
                .block();
    }

    private String buildUrl(Map<String, String> queryParams) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + customersEndpoint);

        if (queryParams != null && !queryParams.isEmpty()) {
            queryParams.forEach((key, value) -> {
                if (value != null && !value.trim().isEmpty()) {
                    builder.queryParam(key, value);
                }
            });
        }

        return builder.build().toUriString();
    }
}
