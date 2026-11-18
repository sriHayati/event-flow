package com.eventflow.booking.dto;
import java.time.LocalDateTime;
public record BookingCreatedEvent(Long bookingId, Long eventId, String customerName,
                                  String customerEmail, Integer quantity, LocalDateTime createdAt) {}
