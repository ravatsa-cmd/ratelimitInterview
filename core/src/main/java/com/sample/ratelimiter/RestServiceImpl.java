package com.sample.ratelimiter;

import com.example.api.rateLimiter.ABCRequest;
import com.example.api.rateLimiter.RestService;
import com.example.api.rateLimiter.messages.ABCResponse;
import com.example.api.rateLimiter.messages.Response;
import org.springframework.http.ResponseEntity;

import java.util.Set;

public class RestServiceImpl implements RestService
{
    @Override public Response someMethod (ABCResponse realmRequest)
    {
        return null;
    }

    @Override public ResponseEntity<Set<ABCRequest>> getSomeMethod ()
    {
        return null;
    }
}
