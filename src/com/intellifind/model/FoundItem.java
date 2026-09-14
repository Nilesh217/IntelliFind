package com.intellifind.model;

import java.time.LocalDate;

public class FoundItem extends Item {
    private String storageLocation;

    public FoundItem(String id, String category, String brand, String model,
                     String color, String location, LocalDate date, String description,
                     ItemStatus status, String storageLocation) {
        super(id, category, brand, model, color, location, date, description, status);
        this.storageLocation = storageLocation;
    }

    public String getStorageLocation() { return storageLocation; }

    @Override
    public String toCsv() {
        return String.join(",", id, category, brand, model, color,
                location.replace(",", ";"), date.toString(),
                description.replace(",", ";"), status.name(),
                storageLocation.replace(",", ";"));
    }

    public static FoundItem fromCsv(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length != 10) {
            throw new IllegalArgumentException("Corrupt FoundItem record (expected 10 tokens, found " + parts.length + ")");
        }
        try {
            return new FoundItem(
                    parts[0].trim(),
                    parts[1].trim(),
                    parts[2].trim(),
                    parts[3].trim(),
                    parts[4].trim(),
                    parts[5].replace(";", ",").trim(),
                    LocalDate.parse(parts[6].trim()),
                    parts[7].replace(";", ",").trim(),
                    ItemStatus.valueOf(parts[8].trim().toUpperCase()),
                    parts[9].replace(";", ",").trim()
            );
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed parsing FoundItem data: " + line, e);
        }
    }
}