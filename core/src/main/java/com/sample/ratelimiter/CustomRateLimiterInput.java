package com.sample.ratelimiter;

public class CustomRateLimiterInput
{
    public String clientId;
    public long timeOfHit;

    public CustomRateLimiterInput (String clientId, long timeOfHit)
    {
        this.clientId = clientId;
        this.timeOfHit = timeOfHit;
    }
}
