package com.yas.gatewayserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtGrantedAuthoritiesConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity http,
            ReactiveJwtAuthenticationConverter jwtAuthenticationConverter
    ) {

        return http
                .csrf(csrf -> csrf.disable())

                .authorizeExchange(exchanges -> exchanges

                        // Public
                        .pathMatchers("/auth/**").permitAll()
                        .pathMatchers("/api/notification/ws/**").permitAll()

                        // Specific (FIRST)
                        .pathMatchers(
                                "/api/categories/salon-owner/**",
                                "/api/notifications/salon-owner/**",
                                "/api/service-offering/salon-owner/**"
                        ).hasRole("SALON_OWNER")

                        // General (AFTER)
                        .pathMatchers(
                                "/api/salons/**",
                                "/api/categories/**",
                                "/api/bookings/**",
                                "/api/users/**",
                                "/api/payments/**",
                                "/api/notification/**",
                                "/api/service-offering/**",
                                "/api/reviews/**"
                        ).hasAnyRole("CUSTOMER", "SALON_OWNER", "ADMIN")

                        .anyExchange().authenticated()
                )

                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
                )

                .build();
    }

    @Bean
    public ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {

        ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {

            Collection<GrantedAuthority> authorities = new ArrayList<>();

            // 1. Extract resource_access roles
            Map<String, Object> resourceAccess = jwt.getClaim("resource_access");

            if (resourceAccess != null && resourceAccess.containsKey("salon-booking-client")) {

                Map<String, Object> client = (Map<String, Object>) resourceAccess.get("salon-booking-client");
                Collection<String> roles = (Collection<String>) client.get("roles");

                if (roles != null) {
                    roles.forEach(role ->
                            authorities.add(new SimpleGrantedAuthority("ROLE_" + role))
                    );
                }
            }

            return Flux.fromIterable(authorities);
        });

        return converter;
    }
}
