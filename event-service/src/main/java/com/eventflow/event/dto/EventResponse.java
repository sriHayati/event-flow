package com.eventflow.event.dto;

import com.eventflow.event.entity.Event;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventResponse(Long id, String name, String description, String venue,
                            LocalDateTime eventDate, BigDecimal ticketPrice,
                            Integer totalTickets, Integer availableTickets) implements java.io.Serializable {
    public static EventResponse from(Event event) {
        return new EventResponse(event.getId(), event.getName(), event.getDescription(),
                event.getVenue(), event.getEventDate(), event.getTicketPrice(),
                event.getTotalTickets(), event.getAvailableTickets());
    }
}
