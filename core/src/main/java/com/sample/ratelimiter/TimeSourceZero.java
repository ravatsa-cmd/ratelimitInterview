package com.sample.ratelimiter;

public final class TimeSourceZero implements ITimeSource
{
    @Override public long currentTimeMillis ()
    {
        return 0;
    }
}
