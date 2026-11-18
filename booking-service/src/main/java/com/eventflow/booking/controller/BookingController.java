package com.eventflow.booking.controller;

import com.eventflow.booking.dto.*;
import com.eventflow.booking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/bookings")
    public ResponseEntity<BookingResponse> create(@Valid @RequestBody BookingRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.create(request, idempotencyKey));
    }

    @GetMapping("/bookings/{bookingId}")
    public BookingResponse getById(@PathVariable Long bookingId) {
        return bookingService.getById(bookingId);
    }

    @GetMapping("/reports/event-sales")
    public List<BookingService.SalesSummary> salesReport() {
        return bookingService.getSalesSummary();
    }
}
