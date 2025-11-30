package com.example.accomodation_service_backend.controller;

import com.example.accomodation_service_backend.dto.*;
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

    @GetMapping("/findAccomodation")
    public AccomodationByIdDTO findAccomodation(
            @RequestParam(required = false) String accomodationSK
    ) {
        return svc.findAccomodationById(accomodationSK);
    }

    @GetMapping("/KPIInformation")
    public List<AccomodationKPIDTO> KPIInformation(
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String useDistrict,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String useCity,
            @RequestParam(required = false) String city
    ) {
        return svc.findKPIInformation(province, useDistrict,district, useCity, city);
    }

    @GetMapping("/getAllAcommodations")
    public List<AccomodationDTO> getAllAcommodations() {
        return svc.getAllAcommodations();
    }
}

