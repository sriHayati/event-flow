package com.eventflow.event.service;

import com.eventflow.event.dto.*;
import com.eventflow.event.entity.Event;
import com.eventflow.event.repository.EventRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional
    @CacheEvict(cacheNames = {"event-detail", "event-search"}, allEntries = true)
    public EventResponse create(EventRequest request) {
        Event event = new Event(request.name(), request.description(), request.venue(),
                request.eventDate(), request.ticketPrice(), request.totalTickets());
        return EventResponse.from(eventRepository.save(event));
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "event-detail", key = "#eventId")
    public EventResponse getById(Long eventId) {
        return eventRepository.findById(eventId).map(EventResponse::from)
                .orElseThrow(() -> new EntityNotFoundException("Event not found: " + eventId));
    }

    @Transactional(readOnly = true)
    public Page<EventResponse> search(String keyword, LocalDateTime fromDate,
                                      LocalDateTime toDate, Pageable pageable) {
        String term = keyword == null || keyword.isBlank() ? null : keyword.trim();
        return eventRepository.searchEvents(term, fromDate, toDate, pageable).map(EventResponse::from);
    }

    @Transactional
    @CacheEvict(cacheNames = {"event-detail", "event-search"}, allEntries = true)
    public EventResponse update(Long eventId, EventRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found: " + eventId));
        event.update(request.name(), request.description(), request.venue(),
                request.eventDate(), request.ticketPrice(), request.totalTickets());
        return EventResponse.from(event);
    }

    @Transactional
    @CacheEvict(cacheNames = {"event-detail", "event-search"}, allEntries = true)
    public void delete(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Event not found: " + eventId);
        }
        eventRepository.deleteById(eventId);
    }

    @Transactional
    @CacheEvict(cacheNames = {"event-detail", "event-search"}, allEntries = true)
    public EventResponse reserve(Long eventId, int quantity) {
        if (quantity < 1) throw new IllegalArgumentException("Quantity must be greater than zero");
        int changed = eventRepository.reserveTickets(eventId, quantity);
        if (changed == 0) {
            if (!eventRepository.existsById(eventId)) {
                throw new EntityNotFoundException("Event not found: " + eventId);
            }
            throw new IllegalStateException("Not enough tickets available");
        }
        return EventResponse.from(eventRepository.findEventNative(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found: " + eventId)));
    }

    @Transactional
    @CacheEvict(cacheNames = {"event-detail", "event-search"}, allEntries = true)
    public EventResponse release(Long eventId, int quantity) {
        if (quantity < 1) throw new IllegalArgumentException("Quantity must be greater than zero");
        int changed = eventRepository.releaseTickets(eventId, quantity);
        if (changed == 0) {
            if (!eventRepository.existsById(eventId)) {
                throw new EntityNotFoundException("Event not found: " + eventId);
            }
            throw new IllegalStateException("Release quantity exceeds ticket capacity");
        }
        return EventResponse.from(eventRepository.findEventNative(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found: " + eventId)));
    }

    @Transactional(readOnly = true)
    public List<String> getAvailableEventNames() {
        return eventRepository.findAll().stream()
                .filter(event -> event.getAvailableTickets() > 0)
                .map(Event::getName)
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }
}
