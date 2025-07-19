package com.foodopia.apiGateway.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

@Configuration
public class ResponseTraceFilter {

    private static final Logger logger = LoggerFactory.getLogger(ResponseTraceFilter.class);

    @Autowired
    FilterUtility filterUtility;

    /**
     * Post-filter logic - executed after microservice response
     * Adds correlation ID to response headers for client tracing
     */
    @Bean
    public GlobalFilter postGlobalFilter() {
        return (exchange, chain) -> {
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
                String correlationId = filterUtility.getCorrelationId(requestHeaders);

                if (correlationId != null) {
                    logger.debug("Updated correlation id to outbound headers: {}", correlationId);
                    exchange.getResponse().getHeaders().add(FilterUtility.CORRELATION_ID, correlationId);
                }
            }));
        };
    }
}