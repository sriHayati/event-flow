package com.eventflow.booking.service;

import com.eventflow.booking.client.EventClient;
import com.eventflow.booking.dto.*;
import com.eventflow.booking.entity.Booking;
import com.eventflow.booking.repository.BookingRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final EventClient eventClient;
    private final KafkaTemplate<String, BookingCreatedEvent> kafkaTemplate;

    public BookingService(BookingRepository bookingRepository, EventClient eventClient,
                          KafkaTemplate<String, BookingCreatedEvent> kafkaTemplate) {
        this.bookingRepository = bookingRepository;
        this.eventClient = eventClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    public BookingResponse create(BookingRequest request, String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("Idempotency-Key header is required");
        }

        var previousBooking = bookingRepository.findByIdempotencyKey(idempotencyKey);
        if (previousBooking.isPresent()) {
            return BookingResponse.from(previousBooking.get());
        }

        EventResponse event = eventClient.getEvent(request.eventId());
        if (event == null) {
            throw new EntityNotFoundException("Event not found: " + request.eventId());
        }

        // Event Service menjadi satu-satunya pemilik data dan kuota event.
        eventClient.reserveTickets(request.eventId(), request.quantity());

        try {
            BigDecimal total = event.ticketPrice().multiply(BigDecimal.valueOf(request.quantity()));
            Booking booking = new Booking(request.eventId(), request.customerName(),
                    request.customerEmail(), request.quantity(), total, idempotencyKey);
            Booking saved = bookingRepository.saveAndFlush(booking);

            var message = new BookingCreatedEvent(saved.getId(), saved.getEventId(),
                    saved.getCustomerName(), saved.getCustomerEmail(), saved.getQuantity(),
                    saved.getCreatedAt());
            kafkaTemplate.send("booking-created", saved.getId().toString(), message);

            return BookingResponse.from(saved);
        } catch (DataIntegrityViolationException ex) {
            // Request paralel dengan key sama bisa sama-sama lolos dari pengecekan awal.
            // Jika unique constraint menolak salah satunya, kembalikan kuota yang sempat di-reserve.
            eventClient.releaseTickets(request.eventId(), request.quantity());
            return bookingRepository.findByIdempotencyKey(idempotencyKey)
                    .map(BookingResponse::from).orElseThrow(() -> ex);
        } catch (RuntimeException ex) {
            // Kompensasi sederhana jika penyimpanan booking gagal setelah kuota berkurang.
            eventClient.releaseTickets(request.eventId(), request.quantity());
            throw ex;
        }
    }

    @Transactional(readOnly = true)
    public BookingResponse getById(Long bookingId) {
        return bookingRepository.findById(bookingId).map(BookingResponse::from)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found: " + bookingId));
    }

    @Transactional(readOnly = true)
    public List<SalesSummary> getSalesSummary() {
        return bookingRepository.getEventSalesSummary().stream()
                .map(row -> new SalesSummary(((Number) row[0]).longValue(),
                        ((Number) row[1]).longValue(), ((Number) row[2]).longValue(),
                        (BigDecimal) row[3]))
                .toList();
    }

    public record SalesSummary(Long eventId, Long bookingCount, Long ticketsSold,
                               BigDecimal totalRevenue) {}
}
