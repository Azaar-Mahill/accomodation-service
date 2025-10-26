package com.example.accomodation_service_backend.service;

import com.example.accomodation_service_backend.model.Accommodation;
import com.example.accomodation_service_backend.repo.AccommodationLocationRepository;
import com.example.accomodation_service_backend.repo.AccommodationRepository;
import com.example.accomodation_service_backend.repo.AccommodationTypeRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccommodationService {

    private final AccommodationTypeRepository accommodationTypeRepository;
    private final AccommodationLocationRepository accommodationLocationRepository;
    private final AccommodationRepository accommodationRepository;

    public AccommodationService(
            AccommodationTypeRepository accommodationTypeRepository,
            AccommodationLocationRepository accommodationLocationRepository,
            AccommodationRepository accommodationRepository
    ) {
        this.accommodationTypeRepository = accommodationTypeRepository;
        this.accommodationLocationRepository = accommodationLocationRepository;
        this.accommodationRepository = accommodationRepository;
    }

    public List<Accommodation> search(Integer month, String environmentType, String accomodationType) {

        List<String> listOfAccommodationTypeSks = new ArrayList<>();
        if ("Any".equalsIgnoreCase(accomodationType)) {
            listOfAccommodationTypeSks = accommodationTypeRepository.findAllAccommodationTypeSks();
        } else {
            listOfAccommodationTypeSks = accommodationTypeRepository.findAccommodationTypeSksByTypeName(accomodationType);
        }
        if(listOfAccommodationTypeSks.size() != 0){
            List<Accommodation> listOfAccommodationsFoundFromTypeSks = new ArrayList<>();

            for (String accommodationTypeSk : listOfAccommodationTypeSks){
                listOfAccommodationsFoundFromTypeSks.addAll(accommodationRepository.getAccomodationsFromTypeSK(accommodationTypeSk));

            }
        }



        /*List<String> listOfAccommodationLocationSks = new ArrayList<>();
        if ("Any".equalsIgnoreCase(environmentType)) {
            listOfAccommodationLocationSks = accommodationLocationRepository.findAllAccommodationLocationSks();
        } else {
            listOfAccommodationLocationSks = accommodationLocationRepository.findAccommodationLocationSksByEnvironment(environmentType);
        }

        List<String> listOfAccommodationSksConsideringSafety = new ArrayList<>();
        if ("Any".equalsIgnoreCase(environment)) {

        } else {

        }

       */

        return new ArrayList<>();

    }

    /*public List<Accommodation> findAll() {
        return repo.findAll();
    }*/
}

