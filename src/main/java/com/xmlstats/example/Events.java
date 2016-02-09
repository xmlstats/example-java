package com.xmlstats.example;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Events {

    @JsonProperty("events_date")
    private String eventsDate;

    @JsonProperty("event")
    private ArrayList<Event> eventList;

    public Events() { }

    public String getEventsDate() {
        return eventsDate;
    }

    public void setEventsDate(String eventsDate) {
        this.eventsDate = eventsDate;
    }

    public ArrayList<Event> getEventList() {
        return eventList;
    }

    public void setEventList(ArrayList<Event> eventList) {
        this.eventList = eventList;
    }

}
