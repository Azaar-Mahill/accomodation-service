package com.example.accomodation_service_backend.controller;

import com.example.accomodation_service_backend.dto.AccomodationDTO;
import com.example.accomodation_service_backend.dto.AccomodationTypeDTO;
import com.example.accomodation_service_backend.dto.AccomodationWeatherDTO;
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
    public List<AccomodationDTO> search(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String environmentType,
            @RequestParam(required = false) String accomodationType
    ) {
        return svc.search(month, environmentType, accomodationType);
    }

    @GetMapping("/weather")
    public List<AccomodationWeatherDTO> weather(
            @RequestParam(required = false) Integer month
    ) {
        return svc.search2(month);
    }

    @GetMapping("/accomodationTypeInformation")
    public List<AccomodationTypeDTO> accomodationTypeInformation(
            @RequestParam(required = false) String accomodationType
    ) {
        return svc.search3(accomodationType);
    }
}

