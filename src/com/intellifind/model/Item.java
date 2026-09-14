package com.intellifind.model;

import java.time.LocalDate;

public abstract class Item {
    protected String id;
    protected String category;
    protected String brand;
    protected String model;
    protected String color;
    protected String location;
    protected LocalDate date;
    protected String description;
    protected ItemStatus status;

    public Item(String id, String category, String brand, String model,
                String color, String location, LocalDate date, String description, ItemStatus status) {
        this.id = id;
        this.category = category;
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.location = location;
        this.date = date;
        this.description = description;
        this.status = status;
    }

    public String getId() { return id; }
    public String getCategory() { return category; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public String getColor() { return color; }
    public String getLocation() { return location; }
    public LocalDate getDate() { return date; }
    public String getDescription() { return description; }
    public ItemStatus getStatus() { return status; }
    public void setStatus(ItemStatus status) { this.status = status; }

    public abstract String toCsv();
}