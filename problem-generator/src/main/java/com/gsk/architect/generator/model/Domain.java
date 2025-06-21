package com.gsk.architect.generator.model;

/**
 * Enum representing different system design problem domains.
 * Each domain has a description to provide context for the type of problems it includes.
 */
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
