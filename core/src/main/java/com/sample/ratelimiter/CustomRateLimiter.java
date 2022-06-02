package com.sample.ratelimiter;

import com.example.api.rateLimiter.GenericRateLimiter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * A custom implementation of the rate limiter.
 */
public class CustomRateLimiter extends GenericRateLimiter
{
    private static final Object Lock = new Object();

    private static Map<String, Queue<Long>> holder = new HashMap<>();
    private static int TIME_LIMIT = 100;
    private static int MAX_HITS = 5;

    public void hitAPI (String key, long timeStamp)
    {
        if (isAllowed(key, timeStamp)) {
            System.out.printf(String.format(
                "There was a hit at :: %d in millis.",
                timeStamp));
        }
        else {
            System.out.printf(String.format(
                "API's are blocked at :: %d in millis.",
                timeStamp));
        }
    }

    /**
     * Handles race conditions as it is synchronized.
     *
     * @param key
     * @param timestamp
     * @return
     */
    @Override public Boolean isAllowed (String key, long timestamp)
    {

        synchronized(Lock) {
            if (!holder.containsKey(key)) {
                updateTimingQueueAndHolder(new LinkedList<>(), timestamp, key);
                return true;
            }
            else {
                Queue<Long> timings = holder.get(key);
                while (!timings.isEmpty() && timestamp - timings.peek() >= TIME_LIMIT) {
                    timings.poll();
                }
                //the remaining queue is the number of hits within the 1000 ms
                if (timings.size() < MAX_HITS) {
                    updateTimingQueueAndHolder(timings, timestamp, key);
                    return true;
                }
            }

            return false;
        }
    }

    private void updateTimingQueueAndHolder(Queue<Long> timings, long timestamp, String key)
    {
        timings.add(timestamp);
        holder.put(key, timings);
    }
}
