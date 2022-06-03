package com.sample.ratelimiter;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * A class to implement the rateLimiter service.
 * Intentionally kep it as a separate main class in case some demos are required.
 */
public class RateLimiter
{
    private static Logger logger = LoggerFactory.getLogger(RateLimiter.class);

    public static final long WINDOW_SIZE_IN_MIN = TimeUnit.MINUTES.toMillis(5);

    public static void main (String[] args)
    {
        Config conf = ConfigFactory.load("application");
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
            logger.info(String.format("%d is the square of %d", sq, count));
            count++;
            sleepTime *= 2;
            logger.info(String.format("Sleeping for %d seconds",
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
