package com.yas.salonservice.metrics;

import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Component;

/**
 * Central Micrometer metrics registry for the salon-service.
 *
 * Metrics exposed:
 *
 * API Layer
 * ---------
 *  salons.api.requests.total        – Counter  | tags: endpoint, method
 *  salons.api.response.time         – Timer    | tags: endpoint, method, status
 *  salons.api.errors.total          – Counter  | tags: endpoint, method, error_type
 *
 * Business Logic
 * --------------
 *  salons.created.success.total     – Counter  (salon successfully saved)
 *  salons.created.failed.total      – Counter  (validation / not-found failure)
 *  salons.updated.success.total     – Counter  (salon successfully updated)
 *  salons.updated.failed.total      – Counter  (salon not found / unauthorised)
 *
 * External Calls
 * --------------
 *  salons.external.call.duration    – Timer    | tags: service, operation
 *  salons.external.call.failures    – Counter  | tags: service, operation
 */
@Component
public class SalonMetrics {

    private final MeterRegistry registry;

    public SalonMetrics(MeterRegistry registry) {
        this.registry = registry;
    }

    // ── API: request counter ────────────────────────────────────────────────

    public void incrementApiRequest(String endpoint, String method) {
        Counter.builder("salons.api.requests.total")
                .description("Total salon API requests")
                .tag("endpoint", endpoint)
                .tag("method", method)
                .register(registry)
                .increment();
    }

    // ── API: error counter ───────────────────────────────────────────────────

    public void incrementApiError(String endpoint, String method, String errorType) {
        Counter.builder("salons.api.errors.total")
                .description("Total salon API errors")
                .tag("endpoint", endpoint)
                .tag("method", method)
                .tag("error_type", errorType)
                .register(registry)
                .increment();
    }

    // ── API: response-time Timer ─────────────────────────────────────────────

    /** Start a new timing sample; stop with {@link #stopApiTimer}. */
    public Timer.Sample startApiTimer() {
        return Timer.start(registry);
    }

    public void stopApiTimer(Timer.Sample sample, String endpoint, String method, String status) {
        sample.stop(Timer.builder("salons.api.response.time")
                .description("Salon API response time")
                .tag("endpoint", endpoint)
                .tag("method", method)
                .tag("status", status)
                .publishPercentileHistogram()
                .register(registry));
    }

    // ── Business logic: create ─────────────────────────────────────────────

    public void incrementSalonCreateSuccess() {
        Counter.builder("salons.created.success.total")
                .description("Total successfully created salons")
                .register(registry)
                .increment();
    }

    public void incrementSalonCreateFailed() {
        Counter.builder("salons.created.failed.total")
                .description("Total failed salon creation attempts")
                .register(registry)
                .increment();
    }

    // ── Business logic: update ─────────────────────────────────────────────

    public void incrementSalonUpdateSuccess() {
        Counter.builder("salons.updated.success.total")
                .description("Total successfully updated salons")
                .register(registry)
                .increment();
    }

    public void incrementSalonUpdateFailed() {
        Counter.builder("salons.updated.failed.total")
                .description("Total failed salon update attempts")
                .register(registry)
                .increment();
    }

    // ── External calls ───────────────────────────────────────────────────────

    /** Start timing an external Feign call. */
    public Timer.Sample startExternalCallTimer() {
        return Timer.start(registry);
    }

    /** Record the duration once the call finishes. */
    public void stopExternalCallTimer(Timer.Sample sample, String service, String operation) {
        sample.stop(Timer.builder("salons.external.call.duration")
                .description("Duration of external service calls from salon-service")
                .tag("service", service)
                .tag("operation", operation)
                .publishPercentileHistogram()
                .register(registry));
    }

    /** Increment failure counter for an external Feign call. */
    public void incrementExternalCallFailure(String service, String operation) {
        Counter.builder("salons.external.call.failures")
                .description("Total failures of external service calls from salon-service")
                .tag("service", service)
                .tag("operation", operation)
                .register(registry)
                .increment();
    }
}
