package com.gsk.architect.generator.model;

public enum Domain {
    ECOMMERCE("E-commerce systems like Amazon/eBay"),
    VIDEO_STREAMING("Video streaming platforms like Netflix"),
    APPLE("Apple ecosystem services"),
    GENERIC("Generic system design problems");

    private final String description;

    Domain(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
