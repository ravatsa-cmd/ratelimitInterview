package com.example.api.rateLimiter;

public interface IGenericRateLimiter
{
    Boolean isAllowed(String key, long timestamp);
}
