package com.sample.ratelimiter;

public final class TimeSourceAdjustable implements ITimeSource
{

    public TimeSourceAdjustable (long now)
    {
        time = now;
    }

    @Override public long currentTimeMillis ()
    {
        return time;
    }

    public void setCurrentTimeMillis (long time)
    {
        this.time = time;
    }

    public void addMillis (long time)
    {
        this.time += time;
    }

    private long time;
}
