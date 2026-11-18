package com.eventflow.booking.client;

import com.eventflow.booking.dto.EventResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.util.Map;

@Component
public class EventClient {
    private final RestClient restClient;

    public EventClient(@Value("${event-service.url:http://localhost:8081}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public EventResponse getEvent(Long eventId) {
        return restClient.get().uri("/api/events/{eventId}", eventId)
                .retrieve().body(EventResponse.class);
    }

    public EventResponse reserveTickets(Long eventId, int quantity) {
        return restClient.post().uri("/api/events/{eventId}/reserve", eventId)
                .contentType(MediaType.APPLICATION_JSON).body(Map.of("quantity", quantity))
                .retrieve().body(EventResponse.class);
    }

    public void releaseTickets(Long eventId, int quantity) {
        restClient.post().uri("/api/events/{eventId}/release", eventId)
                .contentType(MediaType.APPLICATION_JSON).body(Map.of("quantity", quantity))
                .retrieve().toBodilessEntity();
    }
}
