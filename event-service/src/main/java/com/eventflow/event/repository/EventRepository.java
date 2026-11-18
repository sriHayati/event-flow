package com.eventflow.event.repository;

import com.eventflow.event.entity.Event;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query(value = """
        SELECT *
        FROM events e
        WHERE (:keyword IS NULL
               OR LOWER(e.name) LIKE LOWER(CONCAT('%', CAST(:keyword AS text), '%'))
               OR LOWER(e.venue) LIKE LOWER(CONCAT('%', CAST(:keyword AS text), '%')))
          AND (CAST(:fromDate AS timestamp) IS NULL OR e.event_date >= :fromDate)
          AND (CAST(:toDate AS timestamp) IS NULL OR e.event_date <= :toDate)
        ORDER BY e.event_date ASC
        """,
        countQuery = """
        SELECT COUNT(*)
        FROM events e
        WHERE (:keyword IS NULL
               OR LOWER(e.name) LIKE LOWER(CONCAT('%', CAST(:keyword AS text), '%'))
               OR LOWER(e.venue) LIKE LOWER(CONCAT('%', CAST(:keyword AS text), '%')))
          AND (CAST(:fromDate AS timestamp) IS NULL OR e.event_date >= :fromDate)
          AND (CAST(:toDate AS timestamp) IS NULL OR e.event_date <= :toDate)
        """, nativeQuery = true)
    Page<Event> searchEvents(@Param("keyword") String keyword,
                             @Param("fromDate") LocalDateTime fromDate,
                             @Param("toDate") LocalDateTime toDate,
                             Pageable pageable);

    @Modifying
    @Query(value = """
        UPDATE events
        SET available_tickets = available_tickets - :quantity,
            version = version + 1
        WHERE id = :eventId AND available_tickets >= :quantity
        """, nativeQuery = true)
    int reserveTickets(@Param("eventId") Long eventId, @Param("quantity") int quantity);

    @Modifying
    @Query(value = """
        UPDATE events
        SET available_tickets = available_tickets + :quantity,
            version = version + 1
        WHERE id = :eventId AND available_tickets + :quantity <= total_tickets
        """, nativeQuery = true)
    int releaseTickets(@Param("eventId") Long eventId, @Param("quantity") int quantity);

    @Query(value = "SELECT * FROM events WHERE id = :eventId", nativeQuery = true)
    Optional<Event> findEventNative(@Param("eventId") Long eventId);
}
