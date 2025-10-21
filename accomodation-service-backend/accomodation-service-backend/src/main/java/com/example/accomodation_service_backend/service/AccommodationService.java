package com.example.accomodation_service_backend.service;

import com.example.accomodation_service_backend.model.Accommodation;
import com.example.accomodation_service_backend.repo.AccommodationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccommodationService {

    private final AccommodationRepository repo;

    public AccommodationService(AccommodationRepository repo) {
        this.repo = repo;
    }

    public List<Accommodation> search(Integer month, String environment, String type) {
        // 'month' kept for API compatibility with your Angular form (not used yet)
        return repo.search(environment, type);
    }

    public List<Accommodation> findAll() {
        return repo.findAll();
    }
}

