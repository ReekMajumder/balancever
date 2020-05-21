package com.example.balanceverattempt.models;

import android.util.Pair;

import com.github.sundeepk.compactcalendarview.domain.Event;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class User {

    private String id, name, address, phone, email, password;

    private List<java.util.Map.Entry<Date, Event>> eventList;

    public User() {
    }

    public User(String name, String address, String phone, String email, String password) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.password = password;
        eventList = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Map.Entry<Date, Event>> getEventList() {
        return eventList;
    }

    public void setEventList(List<Map.Entry<Date, Event>> eventList) {
        this.eventList = eventList;
    }

    public void addEventsForDay(Date date, List<Event> eventList) {
        for (Event e : eventList) {
            Map.Entry<Date, Event> pair = new AbstractMap.SimpleEntry<>(date, e);
            this.eventList.add(pair);
        }
    }

    public void addSingleEvent(Date date, Event event) {
        Map.Entry<Date, Event> pair = new AbstractMap.SimpleEntry<>(date, event);
        this.eventList.add(pair);
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
