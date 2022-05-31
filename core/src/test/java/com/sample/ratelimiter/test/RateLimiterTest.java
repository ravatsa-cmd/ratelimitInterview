package com.sample.ratelimiter.test;

import com.sample.ratelimiter.ITimeSource;
import com.sample.ratelimiter.TimeSourceZero;
import com.sample.ratelimiter.IRateLimiter;
import com.sample.ratelimiter.RateLimiterTokenBucket;
import com.sample.ratelimiter.TimeSourceAdjustable;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit Tests for the RateLimiterTokenBucket.
 */
public class RateLimiterTest
{
    public ITimeSource timeSourceZero;

    public RateLimiterTest ()
    {
        timeSourceZero = new TimeSourceZero();
    }

    @Test public void testRateLimiterCreationWorksWithoutTimeSourceAndInitAmount ()
    {
        IRateLimiter limiter = RateLimiterTokenBucket.create(1.0, 5.0);
        Assert.assertNotNull(limiter);

        // we should never take 50ms to get here ...
        Assert.assertTrue(950 <= limiter.waitTimeForNoOfTokens(1.0));
        Assert.assertTrue(limiter.waitTimeForNoOfTokens(1.0) <= 1000);
    }

    @Test public void testRateLimiterCreationWorksWithoutTimeSource ()
    {
        IRateLimiter limiter = RateLimiterTokenBucket.create(1.0, 5.0, 0.0);
        Assert.assertNotNull(limiter);

        // we should never take 50ms to get here ...
        Assert.assertTrue(950 <= limiter.waitTimeForNoOfTokens(1.0));
        Assert.assertTrue(limiter.waitTimeForNoOfTokens(1.0) <= 1000);
    }

    @Test public void testRateLimiterDefaultTimeSourceProgressesWhenWeWait ()
    {
        IRateLimiter limiter = RateLimiterTokenBucket.create(1.0, 5.0);
        Assert.assertNotNull(limiter);

        // we should never take 50ms to get here ...
        Assert.assertTrue(950 <= limiter.waitTimeForNoOfTokens(1.0));
        Assert.assertTrue(limiter.waitTimeForNoOfTokens(1.0) <= 1000);
        try {
            Thread.sleep(500L);
        }
        catch (InterruptedException e) {
            // interrupted? Oh, the insolence!
        }
        Assert.assertTrue(450 <= limiter.waitTimeForNoOfTokens(1.0));
        Assert.assertTrue(limiter.waitTimeForNoOfTokens(1.0) <= 500);
    }

    @Test public void testRateLimiterCreationWorksWithTimeSource ()
    {
        IRateLimiter limiter = RateLimiterTokenBucket.create(1.0,
            5.0,
            0.0,
            timeSourceZero);
        Assert.assertNotNull(limiter);

        // time is fixed (thru time source), result must be exact
        Assert.assertEquals(1000, limiter.waitTimeForNoOfTokens(1.0));
    }

    @Test public void testRateLimiterGivesOutInitAmount ()
    {
        IRateLimiter limiter = RateLimiterTokenBucket.create(1.0,
            1.0,
            1.0,
            timeSourceZero);

        Assert.assertTrue(limiter.consumeNoOfTokens());
        Assert.assertFalse(limiter.consumeNoOfTokens());
    }

    @Test public void testRateLimiterDoesNotWaitForInitAmount ()
    {
        IRateLimiter limiter = RateLimiterTokenBucket.create(1.0,
            1.0,
            1.0,
            timeSourceZero);

        Assert.assertEquals(0, limiter.waitTimeForNoOfTokens(1.0));
        Assert.assertEquals(0, limiter.waitTimeForNoOfTokens());
    }

    @Test public void testRateLimiterGivesFreshAmountAfterSomeTime ()
    {
        TimeSourceAdjustable timeSource = new TimeSourceAdjustable(0);

        IRateLimiter limiter = RateLimiterTokenBucket.create(1.0, 1.0, 0.0, timeSource);

        Assert.assertEquals(1000, limiter.waitTimeForNoOfTokens(1.0));
        Assert.assertFalse(limiter.consumeNoOfTokens());
        timeSource.setCurrentTimeMillis(999);
        Assert.assertFalse(limiter.consumeNoOfTokens());

        timeSource.setCurrentTimeMillis(1000);
        Assert.assertTrue(limiter.consumeNoOfTokens());

        Assert.assertFalse(limiter.consumeNoOfTokens());
    }

    @Test public void testRateLimiterDoesNotWaitIfAmountIsAvailable ()
    {
        TimeSourceAdjustable timeSource = new TimeSourceAdjustable(0);

        IRateLimiter limiter = RateLimiterTokenBucket.create(1.0, 1.0, 1.0, timeSource);
        Assert.assertEquals(0, limiter.waitTimeForNoOfTokens(1.0));
    }

    @Test public void testRateLimiterDoesNotWaitIfAmountBecomesAvailable ()
    {
        TimeSourceAdjustable timeSource = new TimeSourceAdjustable(0);

        IRateLimiter limiter = RateLimiterTokenBucket.create(1.0, 2.0, 1.0, timeSource);
        Assert.assertEquals(0, limiter.waitTimeForNoOfTokens(1.0));
        Assert.assertEquals(1000, limiter.waitTimeForNoOfTokens(2.0));
        timeSource.addMillis(1000);
        Assert.assertEquals(0, limiter.waitTimeForNoOfTokens(2.0));
    }

    @Test public void testRateLimiterGivesImpossibleTimeForTooLargeAmount ()
    {
        TimeSourceAdjustable timeSource = new TimeSourceAdjustable(0);

        IRateLimiter limiter = RateLimiterTokenBucket.create(1.0, 2.0, 1.0, timeSource);
        Assert.assertEquals(IRateLimiter.WAIT_IMPOSSIBLE, limiter.waitTimeForNoOfTokens(2.1));
    }

    @Test public void testRateLimiterGivesImpossibleTimeForTooLargeWaitTime ()
    {
        TimeSourceAdjustable timeSource = new TimeSourceAdjustable(0);

        IRateLimiter limiter = RateLimiterTokenBucket.create(1e-9, 1e9, 0.0, timeSource);
        Assert.assertEquals(IRateLimiter.WAIT_IMPOSSIBLE, limiter.waitTimeForNoOfTokens(1e9));
    }

}
