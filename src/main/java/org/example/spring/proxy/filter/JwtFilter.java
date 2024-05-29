package org.example.spring.proxy.filter;

import lombok.extern.slf4j.Slf4j;
import org.example.spring.proxy.jwt.TokenProvider;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JwtFilter implements GlobalFilter, Ordered {

    private final TokenProvider tokenProvider;

    public JwtFilter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders httpHeaders = exchange.getRequest().getHeaders();
        String token = httpHeaders.getFirst(HttpHeaders.AUTHORIZATION);

        if (token == null || token.isEmpty()) {
            log.error("Empty token");
            return unauthorizedResponse(exchange);
        }

        boolean validToken = tokenProvider.isValidToken(token);

        if (!validToken) {
            log.error("Invalid token");
            return unauthorizedResponse(exchange);
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

}
