package com.yas.bookingservice.metrics;

import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Component;

/**
 * Central Micrometer metrics registry for the booking-service.
 *
 * Metrics exposed:
 *
 * API Layer
 * ---------
 *  bookings.api.requests.total        – Counter  | tags: endpoint, method
 *  bookings.api.response.time         – Timer    | tags: endpoint, method, status
 *  bookings.api.errors.total          – Counter  | tags: endpoint, method, error_type
 *
 * Business Logic
 * --------------
 *  bookings.created.success.total     – Counter  (booking saved to DB)
 *  bookings.created.failed.total      – Counter  (slot conflict / validation error)
 *
 * External Calls
 * --------------
 *  bookings.external.call.duration    – Timer    | tags: service, operation
 *  bookings.external.call.failures    – Counter  | tags: service, operation
 */
@Component
public class BookingMetrics {

    // ── API layer ──────────────────────────────────────────────────────────────

    /** Total requests received for a given endpoint + HTTP method. */
    private final MeterRegistry registry;

    public BookingMetrics(MeterRegistry registry) {
        this.registry = registry;
    }

    // ── API: request counter ────────────────────────────────────────────────

    public void incrementApiRequest(String endpoint, String method) {
        Counter.builder("bookings.api.requests.total")
                .description("Total booking API requests")
                .tag("endpoint", endpoint)
                .tag("method", method)
                .register(registry)
                .increment();
    }

    // ── API: error counter ───────────────────────────────────────────────────

    public void incrementApiError(String endpoint, String method, String errorType) {
        Counter.builder("bookings.api.errors.total")
                .description("Total booking API errors")
                .tag("endpoint", endpoint)
                .tag("method", method)
                .tag("error_type", errorType)
                .register(registry)
                .increment();
    }

    // ── API: response-time Timer (returns a Timer.Sample to be stopped by caller) ──

    /**
     * Start a new sample. Stop it with {@link #stopApiTimer(Timer.Sample, String, String, String)}.
     */
    public Timer.Sample startApiTimer() {
        return Timer.start(registry);
    }

    public void stopApiTimer(Timer.Sample sample, String endpoint, String method, String status) {
        sample.stop(Timer.builder("bookings.api.response.time")
                .description("Booking API response time")
                .tag("endpoint", endpoint)
                .tag("method", method)
                .tag("status", status)
                .publishPercentileHistogram()
                .register(registry));
    }

    // ── Business logic ───────────────────────────────────────────────────────

    public void incrementBookingSuccess() {
        Counter.builder("bookings.created.success.total")
                .description("Total successfully created bookings")
                .register(registry)
                .increment();
    }

    public void incrementBookingFailed() {
        Counter.builder("bookings.created.failed.total")
                .description("Total failed booking creation attempts")
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
        sample.stop(Timer.builder("bookings.external.call.duration")
                .description("Duration of external service calls from booking-service")
                .tag("service", service)
                .tag("operation", operation)
                .publishPercentileHistogram()
                .register(registry));
    }

    /** Increment failure counter for an external Feign call. */
    public void incrementExternalCallFailure(String service, String operation) {
        Counter.builder("bookings.external.call.failures")
                .description("Total failures of external service calls from booking-service")
                .tag("service", service)
                .tag("operation", operation)
                .register(registry)
                .increment();
    }
}
