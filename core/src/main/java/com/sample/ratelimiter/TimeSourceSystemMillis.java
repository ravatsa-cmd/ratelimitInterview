package com.sample.ratelimiter;

import com.example.api.rateLimiter.ITimeSource;

public final class TimeSourceSystemMillis implements ITimeSource
{
    @Override public long currentTimeMillis ()
    {
        return System.currentTimeMillis();
    }
}
