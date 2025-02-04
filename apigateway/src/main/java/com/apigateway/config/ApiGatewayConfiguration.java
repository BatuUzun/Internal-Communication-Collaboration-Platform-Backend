package com.apigateway.config;

import java.util.Arrays;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class ApiGatewayConfiguration {

	 @Bean
	    public RouteLocator gatewayRouter(RouteLocatorBuilder builder, JwtAuthFilter jwtAuthFilter) {
	        return builder.routes()
	        		
	        		
	                .route(p -> p.path("/authentication/change-password")
	                        .filters(f -> f.filter(jwtAuthFilter)) // Apply JWT filter only to this path
	                        .uri("lb://authentication"))
	                
	                
	                .route(p -> p.path("/authentication/**")
	                        .uri("lb://authentication")) // Other authentication APIs without JWT filter
	                
	                
	                .route(p -> p.path("/tokengenerator/**")
	                        .uri("lb://tokengenerator")) // No JWT filter for this route
	                
	                
	                .route(p -> p.path("/verification-code-manager/**")
	                        .filters(f -> f.filter(jwtAuthFilter)) // Apply JWT filter to this route
	                        .uri("lb://verification-code-manager"))
	                
	                .route(p -> p.path("/chat-send/**")
	                        .uri("lb://chat-send"))
	                
	                .route(p -> p.path("/chat-search/**")
	                        .uri("lb://chat-search"))
	                
	                
	                .build();
	    }

    /**
     * Configure global CORS settings at the API Gateway level.
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // Define a global CORS configuration
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Arrays.asList("http://localhost:3000", "https://your-frontend-url.com"));
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        corsConfig.setAllowCredentials(true);

        // Apply the CORS configuration globally
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}
