package com.example.api.rateLimiter;

import com.example.api.rateLimiter.messages.ABCResponse;
import com.example.api.rateLimiter.messages.Response;
import org.springframework.http.ResponseEntity;

import java.util.Set;

public interface RestService
{
    Response someMethod(ABCResponse realmRequest);

    ResponseEntity<Set<ABCRequest>> getSomeMethod();
}
