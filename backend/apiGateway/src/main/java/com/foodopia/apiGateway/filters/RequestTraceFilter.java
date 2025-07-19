package com.foodopia.apiGateway.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Order(1) // First executed filter
@Component
public class RequestTraceFilter implements GlobalFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestTraceFilter.class);

    @Autowired
    FilterUtility filterUtility;

    /**
     * Pre-filter logic - executed before routing to microservices
     * Handles correlation ID generation for distributed tracing
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders requestHeaders = exchange.getRequest().getHeaders();

        if (isCorrelationIdPresent(requestHeaders)) {
            logger.debug("foodopia-correlation-id found in RequestTraceFilter: {}",
                    filterUtility.getCorrelationId(requestHeaders));
        } else {
            String correlationId = generateCorrelationId();
            exchange = filterUtility.setCorrelationId(exchange, correlationId);
            logger.debug("foodopia-correlation-id generated in RequestTraceFilter: {}", correlationId);
        }

        return chain.filter(exchange);
    }

    /**
     * Checks if correlation ID exists in request headers
     */
    private boolean isCorrelationIdPresent(HttpHeaders requestHeaders) {
        return filterUtility.getCorrelationId(requestHeaders) != null;
    }

    /**
     * Generates a unique correlation ID using UUID
     */
    private String generateCorrelationId() {
        return java.util.UUID.randomUUID().toString();
    }
}