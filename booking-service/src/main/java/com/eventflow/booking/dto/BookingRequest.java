package com.eventflow.booking.dto;
import jakarta.validation.constraints.*;
public record BookingRequest(
        @NotNull @Positive Long eventId,
        @NotBlank @Size(max = 120) String customerName,
        @NotBlank @Email @Size(max = 180) String customerEmail,
        @NotNull @Min(1) @Max(10) Integer quantity
) {}
