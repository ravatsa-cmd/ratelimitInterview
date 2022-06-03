package com.example.service;

import com.example.api.rateLimiter.ABCRequest;
import com.example.api.rateLimiter.RestService;
import com.example.api.rateLimiter.messages.ABCResponse;
import com.example.api.rateLimiter.messages.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/someContext")
public class ServiceClass
{
    private static Logger logger = LoggerFactory.getLogger(ServiceClass.class);

    @Autowired RestService restService;

    @PostMapping(value = "/postEndPoint") public ResponseEntity<Response> something (@RequestBody Map<String, String> map)
    {
        return new ResponseEntity<>( new ABCResponse(), HttpStatus.OK);
    }

    @GetMapping(value = "/getEndPoint") public ResponseEntity<Set<ABCRequest>> getSomething () throws
        Exception
    {
        ResponseEntity<Set<ABCRequest>> response = restService.getSomeMethod();
        return response;
    }
}
