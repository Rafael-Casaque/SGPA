package com.example.sgpa.domain.entities.historical;

import com.example.sgpa.domain.entities.user.Technician;
import com.example.sgpa.domain.entities.user.User;

import java.time.LocalDateTime;

public class Event {
    private EventType type;
    private final LocalDateTime timeStamp;
    private User requester;
    private Technician technician;
    private int sessionId;

    public Event(EventType type, User requester, Technician technician, int sessionId) {
        this.type = type;
        this.requester = requester;
        this.technician = technician;
        this.sessionId = sessionId;
        this.timeStamp = LocalDateTime.now();
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

}
