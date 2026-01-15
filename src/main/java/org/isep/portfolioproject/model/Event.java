package org.isep.portfolioproject.model;

import org.isep.portfolioproject.util.EventType;

import java.time.LocalDateTime;

public class Event {

    private String id;
    private String title;
    private String description;
    private EventType type;
    private LocalDateTime timestamp;
    private String portfolioId;

    public Event() {
    }

    public Event(String id, String title, String description, EventType type, LocalDateTime timestamp, String portfolioId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.timestamp = timestamp;
        this.portfolioId = portfolioId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    @Override
    public String toString() {
        String when = timestamp == null ? "" : timestamp.toLocalDate().toString();
        String name = title == null ? "Event" : title;
        return when.isEmpty() ? name : when + " - " + name;
    }
}
