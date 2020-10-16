package com.jn.agileway.redis.examples.controller.rest;

import com.jn.agileway.redis.examples.controller.redis_examples.Person;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/rest/global_response")
@RestController
public class GlobalResponseTests {
    @GetMapping
    public Person aPerson(){
        Person p = new Person();
        p.setAge(10);
        p.setName("rest global response body");
        return p;
    }
}
