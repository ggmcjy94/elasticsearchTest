package com.example.elasticsearchTest.controller;

import com.example.elasticsearchTest.document.Person;
import com.example.elasticsearchTest.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/person")
public class PersonController {

    private final PersonService service;

    @Autowired
    public PersonController(PersonService personService) {
        this.service = personService;
    }

    @PostMapping
    public void save(@RequestBody final Person person) {
        service.save(person);
    }


    @GetMapping("/{id}")
    public Person findById(@PathVariable ("id") final String id) {
        return service.findById(id);
    }
}
