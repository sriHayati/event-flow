package com.eventflow.notification.consumer;

import com.eventflow.notification.dto.BookingCreatedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class BookingEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(BookingEventConsumer.class);

    @KafkaListener(topics = "booking-created", groupId = "eventflow-notification")
    public void handleBookingCreated(BookingCreatedMessage event) {
        // Untuk mini project, pengiriman notifikasi ditunjukkan melalui log.
        // Provider email/WhatsApp dapat ditambahkan tanpa mengubah Booking Service.
        log.info("Booking confirmed: bookingId={}, customer={}, email={}, quantity={}",
                event.bookingId(), event.customerName(), event.customerEmail(), event.quantity());
    }
}
