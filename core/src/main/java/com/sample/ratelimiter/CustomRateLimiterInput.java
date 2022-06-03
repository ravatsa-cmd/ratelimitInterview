package com.sample.ratelimiter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomRateLimiterInput
{
    private static Logger logger = LoggerFactory.getLogger(CustomRateLimiterInput.class);

    public String clientId;
    public long timeOfHit;

    public CustomRateLimiterInput (String clientId, long timeOfHit)
    {
        this.clientId = clientId;
        this.timeOfHit = timeOfHit;
    }
}
