package com.example.balanceverattempt.models;

import android.util.Pair;

import com.github.sundeepk.compactcalendarview.domain.Event;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class User {

    private String id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String password;
    private String eventbriteAccessToken;
    private List<Date> eventDateList;

    private List<Event> eventEventList;

    public User() {
    }

    public User(String name, String address, String phone, String email, String password) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.password = password;

        // Initializing eventbriteAccessToken to empty so that null pointer exceptions are not caused
        eventbriteAccessToken = "";

        // Initializing lists
        eventEventList = new ArrayList<>();
        eventDateList = new ArrayList<>();
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

    // Event Date List
    public List<Date> getEventDateList() {
        return eventDateList;
    }

    public void setEventDateList(List<Date> eventDateList) {
        this.eventDateList = eventDateList;
    }

    public void addDayDates(List<Date> dateList){
        for (Date d : dateList){
            eventDateList.add(d);
        }
    }

    public void addDate(Date date){
        eventDateList.add(date);
    }

    public void removeDate(Date date){
        for (Date d : eventDateList){
            if (d.equals(date)){
                eventDateList.remove(date);
            }
        }
    }

    // Event event list
    public List<Event> getEventEventList() {
        return eventEventList;
    }

    public void setEventEventList(List<Event> eventEventList) {
        this.eventEventList = eventEventList;
    }

    public void addEventsForDay(List<Event> eventDayList){
        eventEventList.addAll(eventDayList);
        System.out.println("AddEventsForDay in User (eventEventList): " + eventEventList);
    }

    public void addEvent(Event event){
        eventEventList.add(event);
    }

    public void removeEvent(Event event){
        for (Event e : eventEventList){
            if (e.getData().equals(event.getData()) && e.getTimeInMillis() == event.getTimeInMillis()){
                eventEventList.remove(event);
            }
        }
    }

    public String getEventbriteAccessToken() {
        return eventbriteAccessToken;
    }

    public void setEventbriteAccessToken(String eventbriteAccessToken) {
        this.eventbriteAccessToken = eventbriteAccessToken;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", eventbriteAccessToken='" + eventbriteAccessToken + '\'' +
                ", eventDateList=" + eventDateList +
                ", eventEventList=" + eventEventList +
                '}';
    }
}
