package com.eventflow.event.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventRequest(
        @NotBlank @Size(max = 120) String name,
        @Size(max = 1000) String description,
        @NotBlank @Size(max = 150) String venue,
        @NotNull LocalDateTime eventDate,
        @NotNull @DecimalMin("0.0") BigDecimal ticketPrice,
        @NotNull @Min(1) @Max(1000000) Integer totalTickets
) {}
