package com.example.accomodation_service_backend.controller;

import com.example.accomodation_service_backend.model.Accommodation;
import com.example.accomodation_service_backend.service.AccommodationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accommodations")
@CrossOrigin(origins = "http://localhost:4200")
public class AccommodationController {

    private final AccommodationService svc;

    public AccommodationController(AccommodationService svc) {
        this.svc = svc;
    }

    // GET /api/accommodations/search?month=&environment=&type=
    @GetMapping("/search")
    public List<Accommodation> search(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String environment,
            @RequestParam(required = false) String type
    ) {
        return svc.search(month, environment, type);
    }

    // Optional: fetch all
    @GetMapping
    public List<Accommodation> all() {
        return svc.findAll();
    }
}

