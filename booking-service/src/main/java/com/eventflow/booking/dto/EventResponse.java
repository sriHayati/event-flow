package com.eventflow.booking.dto;
import java.math.BigDecimal;
public record EventResponse(Long id, String name, BigDecimal ticketPrice, Integer availableTickets) {}
