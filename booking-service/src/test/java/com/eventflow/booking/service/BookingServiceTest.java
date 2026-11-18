package com.eventflow.booking.service;

import com.eventflow.booking.client.EventClient;
import com.eventflow.booking.dto.*;
import com.eventflow.booking.entity.Booking;
import com.eventflow.booking.repository.BookingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import java.math.BigDecimal;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock BookingRepository bookingRepository;
    @Mock EventClient eventClient;
    @Mock KafkaTemplate<String, BookingCreatedEvent> kafkaTemplate;
    @InjectMocks BookingService bookingService;

    @Test
    void retryWithSameKeyShouldReturnExistingBooking() {
        Booking existing = new Booking(4L, "Rani", "rani@example.com",
                2, new BigDecimal("120000"), "request-123");
        when(bookingRepository.findByIdempotencyKey("request-123")).thenReturn(Optional.of(existing));

        var result = bookingService.create(
                new BookingRequest(4L, "Rani", "rani@example.com", 2), "request-123");

        assertThat(result.eventId()).isEqualTo(4L);
        verifyNoInteractions(eventClient, kafkaTemplate);
    }
}
