package com.sample.ratelimiter;

public interface IRateLimiter
{
    public static long WAIT_IMPOSSIBLE = Long.MAX_VALUE;

    boolean consumeNoOfTokens ();

    boolean consumeNoOfTokens (double noOfTokens);

    long waitTimeForNoOfTokens ();

    long waitTimeForNoOfTokens (double noOfTokens);

}