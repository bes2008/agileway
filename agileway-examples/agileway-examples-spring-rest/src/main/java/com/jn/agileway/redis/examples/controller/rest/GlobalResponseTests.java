package com.jn.agileway.redis.examples.controller.rest;

import com.jn.agileway.metrics.core.Metric;
import com.jn.agileway.metrics.core.Meters;
import com.jn.agileway.metrics.core.meter.Metered;
import com.jn.agileway.redis.examples.controller.redis_examples.Person;
import com.jn.agileway.web.servlet.RRHolder;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.collection.Collects;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequestMapping("/rest/global_response")
@RestController
public class GlobalResponseTests {
    Metric metricName = Meters.name("http", "qps").tag("instance", "ins-1");
    @GetMapping("/testBean")
    public Person aPerson() {
        Metered meter = Meters.getMeter("test", metricName);
        meter.mark();
        Person p = new Person();
        p.setAge(10);
        p.setName("rest global response body");
        return p;
    }

    @GetMapping("/testListBeans")
    public List<Person> listBeans(){
        Metered meter = Meters.getMeter("test", metricName);
        meter.mark();
        List<Person> list = Collects.newArrayList();
        for(int i =0; i< 3;i++) {
            Person p = new Person();
            p.setAge(10);
            p.setName("rest global response body");
            list.add(p);
        }
        return list;
    }

    @GetMapping("/testResponseEntityBean")
    public ResponseEntity get() {
        Metered meter = Meters.getMeter("test", metricName);
        meter.mark();
        Person p = new Person();
        p.setAge(10);
        p.setName("rest global response body");


        return new ResponseEntity<Person>(p, HttpStatus.OK);
    }


    @GetMapping("/showRequest")
    @PostMapping
    public Object showRequest(){
        Metered meter = Meters.getMeter("test", metricName);
        meter.mark();

        HttpServletRequest request = RRHolder.getRequest();
        String[] values = request.getParameterValues("name");
        return values;
    }

    @GetMapping("/testXss")
    @PostMapping
    public Object testXss(){

        HttpServletRequest request = RRHolder.getRequest();
        String value = request.getParameter("name");
        return StringTemplates.formatWithPlaceholder("<script>{}</script>", value);
    }
}
