package com.codegym.model;

import java.time.LocalDate;

//phương thức bổ trợ để hỗ trợ lấy dữ liệu từ body gửi từ request api bên frontend search
public class SearchRequest {
    private String location;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Integer guests;
    private String sortOrder;
    private Integer minBedrooms;
    private Integer minBathrooms;

    // Getters & Setters
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDate getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDate checkIn) { this.checkIn = checkIn; }

    public LocalDate getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDate checkOut) { this.checkOut = checkOut; }

    public Integer getGuests() { return guests; }
    public void setGuests(Integer guests) { this.guests = guests; }

    public String getSortOrder() { return sortOrder; }
    public void setSortOrder(String sortOrder) { this.sortOrder = sortOrder; }

    public Integer getMinBedrooms() { return minBedrooms; }
    public void setMinBedrooms(Integer minBedrooms) { this.minBedrooms = minBedrooms; }

    public Integer getMinBathrooms() { return minBathrooms; }
    public void setMinBathrooms(Integer minBathrooms) { this.minBathrooms = minBathrooms; }
}
