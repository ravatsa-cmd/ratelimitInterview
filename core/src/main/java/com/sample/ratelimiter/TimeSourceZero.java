package com.sample.ratelimiter;

import com.example.api.rateLimiter.ITimeSource;

public final class TimeSourceZero implements ITimeSource
{
    @Override public long currentTimeMillis ()
    {
        return 0;
    }
}
