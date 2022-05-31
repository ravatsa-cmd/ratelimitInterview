package com.sample.ratelimiter;

public final class TimeSourceSystemMillis implements ITimeSource
{
    @Override public long currentTimeMillis ()
    {
        return System.currentTimeMillis();
    }
}
