package com.eventflow.booking.dto;
import com.eventflow.booking.entity.Booking;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public record BookingResponse(Long id, Long eventId, String customerName, String customerEmail,
                              Integer quantity, BigDecimal totalPrice, String status,
                              LocalDateTime createdAt) {
    public static BookingResponse from(Booking booking) {
        return new BookingResponse(booking.getId(), booking.getEventId(), booking.getCustomerName(),
                booking.getCustomerEmail(), booking.getQuantity(), booking.getTotalPrice(),
                booking.getStatus(), booking.getCreatedAt());
    }
}
