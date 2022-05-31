package com.sample.ratelimiter;

import java.util.Objects;
import java.util.function.Function;

/**
 * The token bucket algorithm can be used for this purpose.
 * Associated to each clientId, assume a bucket of tokens, that has a capacity of 100 tokens and a continuous inflow of 100 tokens per second (or 1 token every 0.01s).
 * Each request from a client takes a token from the bucket with that clientId.
 * If that bucket is empty, the request should not be allowed.
 * Regarding memory, for each bucket, only the timestamp of the last request and the token count needs to be stored.
 * When a new request comes, first add 0.01*(current_time-bucket.timestamp) tokens to the bucket, and update the timestamp on the bucket.
 * If the bucket has more than 0 tokens, take a single token, and allow the request.
 * Else, reject the request
 */
public class RateLimiter_Dummy
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
