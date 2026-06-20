package com.walkmates.model;

import java.util.UUID;

/**
 * A Provider is a pet owner or shelter that publishes {@link Listing}s (see REQUIREMENTS FR-2).
 *
 * <p>Its {@code capacity} caps how many active bookings it may have at once across all its
 * listings — a second limit (alongside the Seeker's trust tier) that the booking decision table
 * in Lab 1 must account for.</p>
 */
public class Provider {

    public static final int MIN_CAPACITY = 1;
    public static final int MAX_CAPACITY = 20;
    public static final int DEFAULT_CAPACITY = 3;

    private final String id;
    private String name;
    private double latitude;
    private double longitude;
    private int capacity;

    public Provider(String name, double latitude, double longitude) {
        this(name, latitude, longitude, DEFAULT_CAPACITY);
    }

    public Provider(String name, double latitude, double longitude, int capacity) {
        this.id = UUID.randomUUID().toString();
        setName(name);
        setLatitude(latitude);
        setLongitude(longitude);
        setCapacity(capacity);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public final void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Provider name is required");
        }
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public final void setLatitude(double latitude) {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90");
        }
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public final void setLongitude(double longitude) {
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180");
        }
        this.longitude = longitude;
    }

    public int getCapacity() {
        return capacity;
    }

    public final void setCapacity(int capacity) {
        if (capacity < MIN_CAPACITY || capacity > MAX_CAPACITY) {
            throw new IllegalArgumentException(
                    "Capacity must be between " + MIN_CAPACITY + " and " + MAX_CAPACITY);
        }
        this.capacity = capacity;
    }
}
