package com.sample.ratelimiter;

import com.example.api.rateLimiter.IRateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class that can be extended to give different flavours of ratelimiting.
 */
public abstract class Limiter implements IRateLimiter
{
    private static Logger logger = LoggerFactory.getLogger(Limiter.class);

    /**
     * Define generic methods based on utility.
     */

}
