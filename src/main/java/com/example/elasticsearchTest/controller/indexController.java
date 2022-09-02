package com.example.elasticsearchTest.controller;

import com.example.elasticsearchTest.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/index")
public class indexController {

    private final IndexService service;

    @Autowired
    public indexController(IndexService service) {
        this.service = service;
    }

    @PostMapping("/recreate")
    public void recreateAllIndices() {
        service.recreateIndices(true);
    }
}
