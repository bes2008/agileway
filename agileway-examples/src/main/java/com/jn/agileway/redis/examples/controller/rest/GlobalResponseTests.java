package com.jn.agileway.redis.examples.controller.rest;

import com.jn.agileway.redis.examples.controller.redis_examples.Person;
import com.jn.agileway.web.filter.rr.RRHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/rest/global_response")
@RestController
public class GlobalResponseTests {

    @GetMapping("/testBean")
    public Person aPerson() {
        Person p = new Person();
        p.setAge(10);
        p.setName("rest global response body");
        return p;
    }

    @GetMapping("/testResponseEntityBean")
    public ResponseEntity get() {
        Person p = new Person();
        p.setAge(10);
        p.setName("rest global response body");


        return new ResponseEntity<Person>(p, HttpStatus.OK);
    }


    @GetMapping("/showRequest")
    @PostMapping
    public Object showRequest(){

        HttpServletRequest request = RRHolder.getRequest();
        String[] values = request.getParameterValues("hello");
        return values;
    }
}
