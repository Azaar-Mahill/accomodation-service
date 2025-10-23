package com.example.accomodation_service_backend.service;

import com.example.accomodation_service_backend.repo.AccommodationTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccommodationService {

    private final AccommodationTypeRepository accommodationTypeRepository;

    public AccommodationService(AccommodationTypeRepository accommodationTypeRepository) {
        this.accommodationTypeRepository = accommodationTypeRepository;
    }

    public void search(Integer month, String environment, String type) {
        // 'month' kept for API compatibility with your Angular form (not used yet)
        List<String> listOfAccommodationTypeSks = accommodationTypeRepository.findSkByTypeName(type);
        for (String accommodationTypeSk : listOfAccommodationTypeSks) {
            System.out.println(accommodationTypeSk);
        }

    }

    /*public List<Accommodation> findAll() {
        return repo.findAll();
    }*/
}

