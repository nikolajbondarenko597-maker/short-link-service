package model;

import java.time.LocalDateTime;
import java.util.UUID;

public class ShortLink {
    private final String shortCode;
    private final String originalUrl;
    private final UUID ownerId;
    private int visitLimit;
    private int currentVisits;
    private final LocalDateTime createdAt;
    private final long ttlSeconds; //время жизни в секундах

    public ShortLink(String shortCode, String originalUrl, UUID ownerId, int visitLimit, long ttlSeconds) {
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
        this.ownerId = ownerId;
        this.visitLimit = visitLimit;
        this.currentVisits = 0;
        this.createdAt = LocalDateTime.now();
        this.ttlSeconds = ttlSeconds;
    }

    public boolean isExpired() {
        return getAgeSeconds() >= ttlSeconds;
    }

    public boolean isLimitReached() {
        return currentVisits >= visitLimit;
    }

    public void incrementVisits() {
        currentVisits++;
    }

    //Геттеры
    public String getShortCode() { return shortCode; }
    public String getOriginalUrl() { return originalUrl; }
    public UUID getOwnerId() { return ownerId; }
    public int getCurrentVisits() { return currentVisits; }
    public int getVisitLimit() { return visitLimit; }
    public long getTtlSeconds() { return ttlSeconds; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    private long getAgeSeconds() {
        return java.time.Duration.between(createdAt, LocalDateTime.now()).getSeconds();
    }
}