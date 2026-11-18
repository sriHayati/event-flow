package com.eventflow.booking.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings", uniqueConstraints =
        @UniqueConstraint(name = "uk_booking_idempotency_key", columnNames = "idempotency_key"))
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "customer_name", nullable = false, length = 120)
    private String customerName;

    @Column(name = "customer_email", nullable = false, length = 180)
    private String customerEmail;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "total_price", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalPrice;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "idempotency_key", nullable = false, length = 100)
    private String idempotencyKey;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected Booking() {}

    public Booking(Long eventId, String customerName, String customerEmail, Integer quantity,
                   BigDecimal totalPrice, String idempotencyKey) {
        this.eventId = eventId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.idempotencyKey = idempotencyKey;
        this.status = "CONFIRMED";
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getEventId() { return eventId; }
    public String getCustomerName() { return customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
