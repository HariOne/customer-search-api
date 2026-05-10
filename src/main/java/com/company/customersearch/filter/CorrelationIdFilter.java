package com.company.customersearch.filter;

import com.company.customersearch.util.CorrelationIdUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CorrelationIdFilter implements Filter {

    private static final String CORRELATION_ID_HEADER = "Correlation-Id";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        String correlationId = httpRequest.getHeader(CORRELATION_ID_HEADER);

        if (correlationId == null || correlationId.trim().isEmpty()) {
            correlationId = CorrelationIdUtil.generateCorrelationId();
        }

        CorrelationIdUtil.setCorrelationId(correlationId);
        httpResponse.setHeader(CORRELATION_ID_HEADER, correlationId);

        log.debug("Processing request with Correlation-Id: {}", correlationId);

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            CorrelationIdUtil.clearCorrelationId();
        }
    }
}
