package com.eventflow.booking.repository;

import com.eventflow.booking.entity.Booking;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByIdempotencyKey(String idempotencyKey);

    @Query(value = """
        SELECT b.event_id AS eventId,
               COUNT(*) AS bookingCount,
               SUM(b.quantity) AS ticketsSold,
               SUM(b.total_price) AS totalRevenue
        FROM bookings b
        WHERE b.status = 'CONFIRMED'
        GROUP BY b.event_id
        ORDER BY totalRevenue DESC
        """, nativeQuery = true)
    List<Object[]> getEventSalesSummary();
}
