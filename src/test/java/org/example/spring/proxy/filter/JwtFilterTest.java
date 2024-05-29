package org.example.spring.proxy.filter;

import org.example.spring.proxy.jwt.TokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class JwtFilterTest {

    private TokenProvider tokenProvider;
    private JwtFilter jwtFilter;
    private ServerWebExchange exchange;
    private GatewayFilterChain chain;
    private ServerHttpRequest request;
    private ServerHttpResponse response;

    @BeforeEach
    public void setUp() {
        tokenProvider = Mockito.mock(TokenProvider.class);
        jwtFilter = new JwtFilter(tokenProvider);
        exchange = mock(ServerWebExchange.class);
        chain = mock(GatewayFilterChain.class);
        request = mock(ServerHttpRequest.class);
        response = mock(ServerHttpResponse.class);
    }

    @Test
    public void givenNoToken_whenFilter_thenUnauthorized() {
        // Given
        HttpHeaders headers = new HttpHeaders();
        when(exchange.getRequest()).thenReturn(request);
        when(request.getHeaders()).thenReturn(headers);
        when(exchange.getResponse()).thenReturn(response);
        when(response.setComplete()).thenReturn(Mono.empty());

        // When
        Mono<Void> result = jwtFilter.filter(exchange, chain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(response).setStatusCode(HttpStatus.UNAUTHORIZED);
        verify(response).setComplete();
    }

    @Test
    public void givenInvalidToken_whenFilter_thenUnauthorized() {
        // Given
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "invalidToken");
        when(exchange.getRequest()).thenReturn(request);
        when(request.getHeaders()).thenReturn(headers);
        when(exchange.getResponse()).thenReturn(response);
        when(response.setComplete()).thenReturn(Mono.empty());
        when(tokenProvider.isValidToken(anyString())).thenReturn(false);

        // When
        Mono<Void> result = jwtFilter.filter(exchange, chain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(response).setStatusCode(HttpStatus.UNAUTHORIZED);
        verify(response).setComplete();
        verify(tokenProvider).isValidToken("invalidToken");
    }

    @Test
    public void givenValidToken_whenFilter_thenChainContinues() {
        // Given
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "validToken");
        when(exchange.getRequest()).thenReturn(request);
        when(request.getHeaders()).thenReturn(headers);
        when(exchange.getResponse()).thenReturn(response);
        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
        when(tokenProvider.isValidToken(anyString())).thenReturn(true);

        // When
        Mono<Void> result = jwtFilter.filter(exchange, chain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(chain).filter(exchange);
        verify(tokenProvider).isValidToken("validToken");
    }
}
