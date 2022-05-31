package com.sample.ratelimiter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * I have used 'sliding window with counters' technique to implement this rate limiter.
 * What I do is instead of rate limiting per second I rate limit per minute.
 * This allows me to create 60 buckets (one for each minute) for each hour for each user.
 * Any time the user has crossed the threshold I reject the requests.
 *
 * Reason for adopting this approach is that it can easily scale to any number of users as each user can have at most 60 buckets keeping track of the number of requests.
 * After every hour the buckets are refreshed.
 *
 * Steps:
 * First we will use a HashMap to store the number of requests for every user.
 * For every user, fill up the map with 60 fixed minute slots.
 * For every request timestamp we will truncate it to the closest rounded minute just passed.
 * If the current timestamp is within the current minute then we increment the counter for current minute if the current request is within the limit
 * We reject the request if it has exceeded the rate limit
 * if the new timestamp has passed the current minute then create the new minute key and set the counter to 1
 * if the current timestamp is in the next hour then delete the entries from the previous hour and create new set of 60 minute slots.
 * Whenever we step into a new hour, all the minute slots are recycled.
 *
 */
public class RateLimitService
{

    public static final long SECOND_IN_MILLIS = TimeUnit.SECONDS.toMillis(1);
    private static final long MINUTE_IN_MILLIS = TimeUnit.MINUTES.toMillis(1);
    private static final long HOUR_IN_MILLIS = TimeUnit.HOURS.toMillis(1);
    private static final long DAY_IN_MILLIS = TimeUnit.DAYS.toMillis(1);;

    private static Map<String, ConcurrentSkipListMap<Long, Integer>> limitMap = new HashMap<>();
    private static Map<String, Integer> hourlyRequestCounter = new HashMap<>();

    private int rateLimitPerMin;
    private int rateLimitPerHour;
    private String userId;

    public RateLimitService (int rateLimitPerMin, int rateLimitPerHour)
    {
        this.rateLimitPerMin = rateLimitPerMin;
        this.rateLimitPerHour = rateLimitPerHour;

        logMessages(String.format("Max requests in minute: %d " + System.lineSeparator() + "Max requests in hour: %d",
            this.rateLimitPerMin,
            this.rateLimitPerHour));

        Random random = new Random();
        setUserId("test" + random.nextInt(60));
        refreshMinuteSlots(userId, System.currentTimeMillis());
    }

    public void setUserId (String userId)
    {
        this.userId = userId;
    }

    /**
     * This is the logic to implement Sliding window.
     *
     * @param requestedTime
     * @param userId
     * @return
     */
    private boolean isAllowed (Long requestedTime, String userId)
    {
        ConcurrentSkipListMap<Long, Integer> times = limitMap.get(userId);
        Long truncatedMin = truncate(requestedTime,
            ChronoUnit.MINUTES); // truncate to the beginning of minute

        if (times != null) {
            Long truncatedHour = truncate(requestedTime,
                ChronoUnit.HOURS); // truncate to the beginning of hour
            Long truncatedDay = truncate(requestedTime,
                ChronoUnit.DAYS); // truncate to the beginning of the day
            if ((truncatedMin - truncatedDay >= DAY_IN_MILLIS) || (
                truncatedMin - truncatedHour >= HOUR_IN_MILLIS))
            {
                refreshMinuteSlots(userId, requestedTime);
                hourlyRequestCounter.put(userId, 0);
            }
        }
        else {
            refreshMinuteSlots(userId, requestedTime);
        }

        times = limitMap.get(userId);

        if (null == times || times.isEmpty()) {
            logMessages("times is null or empty returning false.");
            return false;
        }

        if ((times.get(truncatedMin) >= this.rateLimitPerMin) || (
            hourlyRequestCounter.getOrDefault(userId, 0) >= this.rateLimitPerHour))
        {
            logMessages("Rate limit threshold reached, returning false.");
            return false;
        }

        logMessages(String.format("Putting timestamp %d in the bucket %d",
            requestedTime,
            truncatedMin));
        times.put(truncatedMin, times.get(truncatedMin) + 1);
        limitMap.put(userId, times);
        hourlyRequestCounter.put(userId,
            hourlyRequestCounter.getOrDefault(userId, 0) + 1);

        return true;
    }

    /**
     * Truncate the time to closest Unit (Hours in this case)
     *
     * @param time
     * @param unit
     * @return
     */
    private static long truncate(long time, ChronoUnit unit) {
        Instant instant = Instant.ofEpochMilli(time);
        Instant returnValue = instant.truncatedTo(unit);
        return returnValue.toEpochMilli();
    }

    /**
     * Code to add new set of minute wise slots.
     *
     * @param userId
     * @param requestedTime
     */
    private void refreshMinuteSlots (String userId, Long requestedTime)
    {
        logMessages("Working with userID " + userId);
        Long minOfDay = truncate(requestedTime, ChronoUnit.HOURS);
        ConcurrentSkipListMap<Long, Integer> timeStamp_minWise = new ConcurrentSkipListMap<>();

        for (int i = 0; i <= 59; i++) {
            timeStamp_minWise.put(minOfDay, 0);
            minOfDay += MINUTE_IN_MILLIS;
        }

        limitMap.put(userId, timeStamp_minWise);
    }

    /**
     * Method to print the timestamps and corresponding requests for a user.
     */
    public void printTimes ()
    {
        ConcurrentSkipListMap<Long, Integer> times = limitMap.get(userId);
        logMessages("Timestamp vs requests for user... " + userId);
        for (Long time : times.keySet()) {
            logMessages(time + " : " + times.get(time));
        }
    }

    /**
     * Utility method to add logs.
     * Currently using sysouts.
     *
     * @param message
     */
    public static void logMessages (String message)
    {
        if (null == message || message.length() == 0 || message == " ") {
            return;
        }

        System.out.println(message);
    }

    /**
     * The wrap method to rate limit the function calls.
     *
     * @param function
     * @return
     */
    public Function<Integer, Integer> wrap (Function function)
    {
        return isCallAllowed() ? function : Function.identity();
    }

    private Boolean isCallAllowed ()
    {
        long currentTimeMillis = System.currentTimeMillis();
        boolean isAllowed = isAllowed(currentTimeMillis, userId);
        logMessages(String.format(
            "Request at %d isAllowed? %s totalRequestsInHour: %d ",
            currentTimeMillis,
            isAllowed,
            hourlyRequestCounter.get(userId)));

        return Boolean.valueOf(isAllowed);
    }
}

