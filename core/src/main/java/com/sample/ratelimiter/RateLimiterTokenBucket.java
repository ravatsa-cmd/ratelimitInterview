package com.sample.ratelimiter;

public class RateLimiterTokenBucket implements IRateLimiter
{

  private double ratePerMillis;
  private double maxNoOfTokens;
  private double currentNoOfTokens;
  private long lastTimeMillis;
  private ITimeSource timeSource;
  private static ITimeSource defaultTimeSource = new TimeSourceSystemMillis();

  public static IRateLimiter create (double ratePerSecond, double maxNoOfTokens)
  {
    return create(ratePerSecond, maxNoOfTokens, 0.0, defaultTimeSource);
  }

  public static IRateLimiter create (double ratePerSecond,
                                     double maxNoOfTokens,
                                     double initNoOfTokens)
  {
    return create(ratePerSecond, maxNoOfTokens, initNoOfTokens, defaultTimeSource);
  }

  public static IRateLimiter create (double ratePerSecond,
                                     double maxNoOfTokens,
                                     double initNoOfTokens,
                                     ITimeSource source)
  {
    return new RateLimiterTokenBucket(ratePerSecond, maxNoOfTokens, initNoOfTokens, source);
  }

  public RateLimiterTokenBucket (double ratePerSecond,
                                 double maxNoOfTokens,
                                 double initNoOfTokens,
                                 ITimeSource timeSource)
  {
    ratePerMillis = ratePerSecond / 1000.0;
    this.maxNoOfTokens = maxNoOfTokens;
    currentNoOfTokens = initNoOfTokens;
    this.timeSource = timeSource;
    lastTimeMillis = timeSource.currentTimeMillis();
  }

  @Override public boolean consumeNoOfTokens ()
  {
    return consumeNoOfTokens(1.0);
  }

  @Override public boolean consumeNoOfTokens (double noOfTokens)
  {
    updateNoOfTokens();
    boolean success = currentNoOfTokens >= noOfTokens;
    if (success) {
      currentNoOfTokens -= noOfTokens;
    }
    return success;
  }

  @Override public long waitTimeForNoOfTokens ()
  {
    return waitTimeForNoOfTokens(1.0);
  }

  @Override public long waitTimeForNoOfTokens (double noOfTokens)
  {
    if (noOfTokens <= currentNoOfTokens) {
      return 0;
    }

    updateNoOfTokens();

    if (noOfTokens <= currentNoOfTokens) {
      return 0;
    }

    if (noOfTokens > maxNoOfTokens) {
      return IRateLimiter.WAIT_IMPOSSIBLE;
    }

    double lack = noOfTokens - currentNoOfTokens;
    double waitTime = Math.ceil(lack / ratePerMillis);
    if (waitTime > IRateLimiter.WAIT_IMPOSSIBLE) {
      return IRateLimiter.WAIT_IMPOSSIBLE;
    }
    return (long)waitTime;
  }

  /**
   * This method is responsible for updating the number of available tokens for the clientID.
   */
  private void updateNoOfTokens ()
  {
    long now = timeSource.currentTimeMillis();
    long delta = now - lastTimeMillis;
    if (delta > 0) {
      double adjust = delta * ratePerMillis;
      adjust += currentNoOfTokens;
      currentNoOfTokens = Math.min(adjust, maxNoOfTokens);
      lastTimeMillis = now;
    }
  }

}
