package com.eventflow.notification.dto;
import java.time.LocalDateTime;
public record BookingCreatedMessage(Long bookingId, Long eventId, String customerName,
                                    String customerEmail, Integer quantity, LocalDateTime createdAt) {}
