package com.example.api.rateLimiter;

public interface IRateLimiterBlocking extends IRateLimiter
{
    void waitForLimit (double noOfTokens) throws InterruptedException;

    boolean consumeNoOfTokens ();

    boolean consumeNoOfTokens (double noOfTokens);

    long waitTimeForNoOfTokens ();

    long waitTimeForNoOfTokens (double noOfTokens);

}
