package com.example.booking.dto;

import java.math.BigDecimal;

public class ReservationFilterDto {
    private String status;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    public ReservationFilterDto() {}

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getMinPrice() { return minPrice; }
    public void setMinPrice(BigDecimal minPrice) { this.minPrice = minPrice; }
    public BigDecimal getMaxPrice() { return maxPrice; }
    public void setMaxPrice(BigDecimal maxPrice) { this.maxPrice = maxPrice; }
}

