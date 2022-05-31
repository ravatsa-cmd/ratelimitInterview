package com.sample.ratelimiter;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Main class for Rate limiter service.
 */
public class RateLimiter
{

    public static final long WINDOW_SIZE_IN_MIN = TimeUnit.MINUTES.toMillis(5);

    public static void main (String[] args)
    {
        Config conf = ConfigFactory.load("application");
        ApplicationConfig appConfig = new ApplicationConfig(conf);
        Function<Integer, Integer> square = new Function<Integer, Integer>()
        {
            @Override public Integer apply (Integer integer)
            {
                return integer * integer;
            }
        };

        RateLimitService rateLimitService = new RateLimitService(3, 10);

        long sleepTime = RateLimitService.SECOND_IN_MILLIS;

        /**
         * This variable is just to get number for square function.
         */
        int count = 0;

        while (sleepTime <= WINDOW_SIZE_IN_MIN) {
            Function<Integer, Integer> rateLimitedSquare = rateLimitService.wrap(square);
            Integer sq = rateLimitedSquare.apply(count);
            RateLimitService.logMessages(String.format("%d is the square of %d", sq, count));
            count++;
            sleepTime *= 2;
            RateLimitService.logMessages(String.format("Sleeping for %d seconds",
                sleepTime / 1000));
            try {
                Thread.sleep(sleepTime);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        rateLimitService.printTimes();
    }
}
