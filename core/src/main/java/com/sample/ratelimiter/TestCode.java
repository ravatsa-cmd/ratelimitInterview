package com.sample.ratelimiter;

import com.example.api.rateLimiter.IRateLimiter;

import java.util.Objects;
import java.util.function.Function;

public class TestCode
{
    public Function<Integer, Integer> wrap (Function<Integer, Integer> function)
    {
        IRateLimiter limiter = RateLimiterTokenBucket.create(1.0,
         5.0,
         1.0,
        new TimeSourceAdjustable(0));

        if (!Objects.equals(
            IRateLimiter.WAIT_IMPOSSIBLE,
            limiter.waitTimeForNoOfTokens()))
        {
            return Function.identity();
        }

        return null;
    }
}
