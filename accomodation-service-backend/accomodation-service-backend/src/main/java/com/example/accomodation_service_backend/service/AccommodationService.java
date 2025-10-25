package com.example.accomodation_service_backend.service;

import com.example.accomodation_service_backend.repo.AccommodationLocationRepository;
import com.example.accomodation_service_backend.repo.AccommodationTypeRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccommodationService {

    private final AccommodationTypeRepository accommodationTypeRepository;
    private final AccommodationLocationRepository accommodationLocationRepository;

    public AccommodationService(AccommodationTypeRepository accommodationTypeRepository, AccommodationLocationRepository accommodationLocationRepository) {
        this.accommodationTypeRepository = accommodationTypeRepository;
        this.accommodationLocationRepository = accommodationLocationRepository;
    }

    public void search(Integer month, String environment, String environmentType) {

        List<String> listOfAccommodationTypeSks = new ArrayList<>();
        if ("Any".equalsIgnoreCase(environmentType)) {
            listOfAccommodationTypeSks = accommodationTypeRepository.findAllAccommodationTypeSks();
        } else {
            listOfAccommodationTypeSks = accommodationTypeRepository.findAccommodationTypeSksByTypeName(environmentType);
        }

        List<String> listOfAccommodationLocationSks = new ArrayList<>();
        if ("Any".equalsIgnoreCase(environment)) {
            listOfAccommodationLocationSks = accommodationLocationRepository.findAllAccommodationLocationSks();
        } else {
            listOfAccommodationLocationSks = accommodationLocationRepository.findAccommodationLocationSksByEnvironment(environment);
        }

        for (String accommodationTypeSk : listOfAccommodationTypeSks) {
            System.out.println(accommodationTypeSk);
        }

    }

    /*public List<Accommodation> findAll() {
        return repo.findAll();
    }*/
}

