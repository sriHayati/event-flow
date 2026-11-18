package com.eventflow.event.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, length = 150)
    private String venue;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @Column(name = "ticket_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal ticketPrice;

    @Column(name = "total_tickets", nullable = false)
    private Integer totalTickets;

    @Column(name = "available_tickets", nullable = false)
    private Integer availableTickets;

    @Version
    private Long version;

    protected Event() {}

    public Event(String name, String description, String venue, LocalDateTime eventDate,
                 BigDecimal ticketPrice, Integer totalTickets) {
        this.name = name;
        this.description = description;
        this.venue = venue;
        this.eventDate = eventDate;
        this.ticketPrice = ticketPrice;
        this.totalTickets = totalTickets;
        this.availableTickets = totalTickets;
    }

    public void update(String name, String description, String venue, LocalDateTime eventDate,
                       BigDecimal ticketPrice, Integer totalTickets) {
        int sold = this.totalTickets - this.availableTickets;
        if (totalTickets < sold) {
            throw new IllegalArgumentException("Total tickets cannot be lower than tickets already sold");
        }
        this.name = name;
        this.description = description;
        this.venue = venue;
        this.eventDate = eventDate;
        this.ticketPrice = ticketPrice;
        this.totalTickets = totalTickets;
        this.availableTickets = totalTickets - sold;
    }

    public void release(int quantity) {
        if (quantity < 1 || availableTickets + quantity > totalTickets) {
            throw new IllegalArgumentException("Invalid release quantity");
        }
        availableTickets += quantity;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getVenue() { return venue; }
    public LocalDateTime getEventDate() { return eventDate; }
    public BigDecimal getTicketPrice() { return ticketPrice; }
    public Integer getTotalTickets() { return totalTickets; }
    public Integer getAvailableTickets() { return availableTickets; }
    public Long getVersion() { return version; }
}
