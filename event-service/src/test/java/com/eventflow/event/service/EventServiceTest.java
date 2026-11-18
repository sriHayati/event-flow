package com.eventflow.event.service;

import com.eventflow.event.dto.EventRequest;
import com.eventflow.event.entity.Event;
import com.eventflow.event.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @Mock EventRepository eventRepository;
    @InjectMocks EventService eventService;

    @Test
    void createShouldSetInitialAvailableTicketCount() {
        var request = new EventRequest("Backend Meetup", "Tech sharing", "Jakarta",
                LocalDateTime.now().plusDays(10), new BigDecimal("50000"), 80);
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = eventService.create(request);

        assertThat(response.totalTickets()).isEqualTo(80);
        assertThat(response.availableTickets()).isEqualTo(80);
        verify(eventRepository).save(any(Event.class));
    }
}
