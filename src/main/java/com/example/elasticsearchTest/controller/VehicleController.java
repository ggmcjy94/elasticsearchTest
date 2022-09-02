package com.example.elasticsearchTest.controller;

import com.example.elasticsearchTest.document.Vehicle;
import com.example.elasticsearchTest.search.SearchRequestDTO;
import com.example.elasticsearchTest.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicle")
public class VehicleController {

    private final VehicleService service;

    @Autowired
    public VehicleController(VehicleService service) {
        this.service = service;
    }

    @PostMapping
    public void index(@RequestBody final Vehicle vehicle) {
        service.save(vehicle);
    }

    @GetMapping("/{id}")
    public Vehicle getById (@PathVariable("id") final String id) {
        return service.getById(id);
    }

    @PostMapping("/search")
    public List<Vehicle> search (@RequestBody SearchRequestDTO dto) {
        return service.search(dto);
    }
}
