package com.apigateway.config;

import java.time.Duration;
import java.util.Arrays;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import com.apigateway.dto.RefreshTokenResponseDTO;
import com.apigateway.dto.TokenRefreshRequestDTO;
import com.apigateway.util.JwtUtil;

import reactor.core.publisher.Mono;

@Component
public class JwtAuthFilter implements GatewayFilter {

    private final WebClient.Builder webClientBuilder;

    public JwtAuthFilter(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String authToken = null;
        String refreshToken = null;

        // Extract tokens from the Cookie header
        String cookieHeader = headers.getFirst(HttpHeaders.COOKIE);
        if (cookieHeader != null) {
            authToken = Arrays.stream(cookieHeader.split(";"))
                    .map(String::trim)
                    .filter(cookie -> cookie.startsWith("authToken="))
                    .map(cookie -> cookie.substring("authToken=".length()))
                    .findFirst()
                    .orElse(null);

            refreshToken = Arrays.stream(cookieHeader.split(";"))
                    .map(String::trim)
                    .filter(cookie -> cookie.startsWith("refreshToken="))
                    .map(cookie -> cookie.substring("refreshToken=".length()))
                    .findFirst()
                    .orElse(null);
        }


        if (authToken == null || authToken.isEmpty()) {
            // If authToken is missing, try to refresh using the refreshToken
            if (refreshToken != null) {
                return refreshAccessToken(refreshToken, exchange)
                        .flatMap(newAuthToken -> {
                            // Set the new authToken in cookies
                            ResponseCookie newAccessCookie = ResponseCookie.from("authToken", newAuthToken)
                                    .httpOnly(true)
                                    .secure(false)
                                    .path("/")
                                    .maxAge(Duration.ofMinutes(1))
                                    .sameSite("Strict")
                                    .build();

                            exchange.getResponse().getHeaders().add(HttpHeaders.SET_COOKIE, newAccessCookie.toString());

                            return chain.filter(exchange); // Proceed with the request
                        })
                        .onErrorResume(refreshException -> {
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();
                        });
            } else {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        }

        try {
            // Validate the authToken if it exists
            JwtUtil.validateToken(authToken);
            return chain.filter(exchange); // If valid, proceed with the request
        } catch (Exception e) {
            if (refreshToken != null) {
                // Attempt to refresh using the refreshToken
                return refreshAccessToken(refreshToken, exchange)
                        .flatMap(newAuthToken -> {
                            // Set the new authToken in cookies
                            ResponseCookie newAccessCookie = ResponseCookie.from("authToken", newAuthToken)
                                    .httpOnly(true)
                                    .secure(false)
                                    .path("/")
                                    .maxAge(Duration.ofMinutes(1))
                                    .sameSite("Strict")
                                    .build();

                            exchange.getResponse().getHeaders().add(HttpHeaders.SET_COOKIE, newAccessCookie.toString());

                            return chain.filter(exchange); // Proceed with the request
                        })
                        .onErrorResume(refreshException -> {
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();
                        });
            } else {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        }
    }


    private Mono<String> refreshAccessToken(String refreshToken, ServerWebExchange exchange) {
        WebClient webClient = webClientBuilder.build();

        TokenRefreshRequestDTO requestDTO = new TokenRefreshRequestDTO(refreshToken);


        return webClient.post()
                .uri("http://tokengenerator/tokengenerator/refresh")
                .bodyValue(requestDTO)
                .retrieve()
                .bodyToMono(RefreshTokenResponseDTO.class)
                .flatMap(response -> {
                    if (response == null || response.getAccessToken() == null) {
                        return Mono.error(new RuntimeException("Received null access token from tokengenerator."));
                    }
                    return Mono.just(response.getAccessToken());
                })
                .doOnError(error -> System.out.println("Error refreshing token: " + error.getMessage()));
    }





}

