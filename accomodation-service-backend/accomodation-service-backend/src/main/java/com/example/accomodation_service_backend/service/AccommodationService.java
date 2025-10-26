package com.example.accomodation_service_backend.service;

import com.example.accomodation_service_backend.model.Accommodation;
import com.example.accomodation_service_backend.model.SafetyOfAreaOfAccommodation;
import com.example.accomodation_service_backend.repo.AccommodationLocationRepository;
import com.example.accomodation_service_backend.repo.AccommodationRepository;
import com.example.accomodation_service_backend.repo.AccommodationTypeRepository;
import com.example.accomodation_service_backend.repo.SafetyOfAreaOfAccommodationRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class AccommodationService {

    private final AccommodationTypeRepository accommodationTypeRepository;
    private final AccommodationLocationRepository accommodationLocationRepository;
    private final AccommodationRepository accommodationRepository;
    private final SafetyOfAreaOfAccommodationRepository safetyOfAreaOfAccommodationRepository;

    private final BigDecimal weightOfAccidents = BigDecimal.valueOf(2);

    private final BigDecimal weightOfCrimes = BigDecimal.valueOf(1);

    private final BigDecimal safetyThreshold = BigDecimal.valueOf(2);

    public AccommodationService(
            AccommodationTypeRepository accommodationTypeRepository,
            AccommodationLocationRepository accommodationLocationRepository,
            AccommodationRepository accommodationRepository,
            SafetyOfAreaOfAccommodationRepository safetyOfAreaOfAccommodationRepository
    ) {
        this.accommodationTypeRepository = accommodationTypeRepository;
        this.accommodationLocationRepository = accommodationLocationRepository;
        this.accommodationRepository = accommodationRepository;
        this.safetyOfAreaOfAccommodationRepository = safetyOfAreaOfAccommodationRepository;
    }

    public List<Accommodation> search(Integer month, String environmentType, String accomodationType) {

        List<String> listOfAccommodationTypeSks = new ArrayList<>();
        if ("Any".equalsIgnoreCase(accomodationType)) {
            listOfAccommodationTypeSks = accommodationTypeRepository.findAllAccommodationTypeSks();
        } else {
            listOfAccommodationTypeSks = accommodationTypeRepository.findAccommodationTypeSksByTypeName(accomodationType);
        }
        if(listOfAccommodationTypeSks.size() != 0){
            List<Accommodation> listOfAccommodationsFoundFromAccommodationTypeSks = new ArrayList<>();

            for (String accommodationTypeSk : listOfAccommodationTypeSks){
                listOfAccommodationsFoundFromAccommodationTypeSks.addAll(accommodationRepository.getAccomodationsFromTypeSK(accommodationTypeSk));

            }

            List<Accommodation> listOfAccommodationsFoundFromEnvironmentTypeSks = new ArrayList<>();

            if ("Any".equalsIgnoreCase(environmentType)) {
                listOfAccommodationsFoundFromEnvironmentTypeSks.addAll(listOfAccommodationsFoundFromAccommodationTypeSks);
            } else {
                for (Accommodation accommodationsFoundFromAccommodationTypeSks : listOfAccommodationsFoundFromAccommodationTypeSks){
                    String environmentOfTheLocation = accommodationLocationRepository.getEnvironment(accommodationsFoundFromAccommodationTypeSks.getAccommodationLocationSk());
                    if(environmentOfTheLocation.equals(environmentType)){
                        listOfAccommodationsFoundFromEnvironmentTypeSks.add(accommodationsFoundFromAccommodationTypeSks);
                    }
                }
            }

            List<Accommodation> listOfAccommodationsFoundFromFilteringSafety = new ArrayList<>();

            for (Accommodation accommodationFoundFromEnvironmentTypeSks: listOfAccommodationsFoundFromEnvironmentTypeSks){
                String locationSKOfAccomodation = accommodationFoundFromEnvironmentTypeSks.getAccommodationLocationSk();
                String convertedMonthSK = convertToMonthSK(month);

                SafetyOfAreaOfAccommodation safetyOfAreaOfAccommodation = safetyOfAreaOfAccommodationRepository.getSafetyDetailsOfLocation(locationSKOfAccomodation, convertedMonthSK);

                BigDecimal accident = (safetyOfAreaOfAccommodation != null && safetyOfAreaOfAccommodation.getAccidentRate() != null)
                        ? safetyOfAreaOfAccommodation.getAccidentRate()
                        : BigDecimal.ZERO;

                BigDecimal crime = (safetyOfAreaOfAccommodation != null && safetyOfAreaOfAccommodation.getCrimeRate() != null)
                        ? safetyOfAreaOfAccommodation.getCrimeRate()
                        : BigDecimal.ZERO;

                BigDecimal safetyRateOfAccommodation = (weightOfAccidents.multiply(accident)).add(weightOfCrimes.multiply(crime));
                if (safetyRateOfAccommodation.compareTo(safetyThreshold) < 0) {
                    listOfAccommodationsFoundFromFilteringSafety.add(accommodationFoundFromEnvironmentTypeSks);
                }
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

    public String convertToMonthSK(Integer month) {
        if(month>9){
            return "2025-"+month;
        }else{
            return "2025-0"+month;
        }

    }

    /*public List<Accommodation> findAll() {
        return repo.findAll();
    }*/
}

