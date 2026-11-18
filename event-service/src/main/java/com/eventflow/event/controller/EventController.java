package com.eventflow.event.controller;

import com.eventflow.event.dto.*;
import com.eventflow.event.service.EventService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<EventResponse> create(@Valid @RequestBody EventRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.create(request));
    }

    @GetMapping("/{eventId}")
    public EventResponse getById(@PathVariable Long eventId) {
        return eventService.getById(eventId);
    }

    @GetMapping
    public Page<EventResponse> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @PageableDefault(size = 10) Pageable pageable) {
        return eventService.search(keyword, fromDate, toDate, pageable);
    }

    @PutMapping("/{eventId}")
    public EventResponse update(@PathVariable Long eventId, @Valid @RequestBody EventRequest request) {
        return eventService.update(eventId, request);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> delete(@PathVariable Long eventId) {
        eventService.delete(eventId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/available-names")
    public List<String> availableNames() {
        return eventService.getAvailableEventNames();
    }

    @PostMapping("/{eventId}/reserve")
    public EventResponse reserve(@PathVariable Long eventId, @RequestBody Map<String, Integer> body) {
        return eventService.reserve(eventId, body.getOrDefault("quantity", 0));
    }

    @PostMapping("/{eventId}/release")
    public EventResponse release(@PathVariable Long eventId, @RequestBody Map<String, Integer> body) {
        return eventService.release(eventId, body.getOrDefault("quantity", 0));
    }
}
