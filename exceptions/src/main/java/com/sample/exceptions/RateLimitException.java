package com.sample.exceptions;

public class RateLimitException extends Exception
{
    public RateLimitException (String message)
    {
        super(message);
    }

    public RateLimitException (String message, Throwable cause)
    {
        super(message, cause);
    }
}
