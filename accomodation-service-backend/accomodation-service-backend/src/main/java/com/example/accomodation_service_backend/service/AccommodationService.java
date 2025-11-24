package com.example.accomodation_service_backend.service;

import com.example.accomodation_service_backend.dto.AccomodationDTO;
import com.example.accomodation_service_backend.model.Accommodation;
import com.example.accomodation_service_backend.model.SafetyOfAreaOfAccommodation;
import com.example.accomodation_service_backend.model.WeatherOfAreaOfAccommodation;
import com.example.accomodation_service_backend.repo.*;
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

    private final WeatherOfAreaOfAccommodationRepository weatherOfAreaOfAccommodationRepository;

    private final BigDecimal weightOfAccidents = BigDecimal.valueOf(2);

    private final BigDecimal weightOfCrimes = BigDecimal.valueOf(1);

    private final BigDecimal safetyThreshold = BigDecimal.valueOf(2);

    public AccommodationService(
            AccommodationTypeRepository accommodationTypeRepository,
            AccommodationLocationRepository accommodationLocationRepository,
            AccommodationRepository accommodationRepository,
            SafetyOfAreaOfAccommodationRepository safetyOfAreaOfAccommodationRepository,
            WeatherOfAreaOfAccommodationRepository weatherOfAreaOfAccommodationRepository
    ) {
        this.accommodationTypeRepository = accommodationTypeRepository;
        this.accommodationLocationRepository = accommodationLocationRepository;
        this.accommodationRepository = accommodationRepository;
        this.safetyOfAreaOfAccommodationRepository = safetyOfAreaOfAccommodationRepository;
        this.weatherOfAreaOfAccommodationRepository = weatherOfAreaOfAccommodationRepository;
    }

    public List<AccomodationDTO> search(Integer month, String environmentType, String accomodationType) {

        List<String> listOfAccommodationTypeSks = new ArrayList<>();
        if ("Any".equalsIgnoreCase(accomodationType)) {
            listOfAccommodationTypeSks = accommodationTypeRepository.findAllAccommodationTypeSks();
        } else {
            listOfAccommodationTypeSks = accommodationTypeRepository.findAccommodationTypeSksByTypeName(accomodationType);
        }

        if(listOfAccommodationTypeSks.size() == 0){
            return new ArrayList<>();
        }

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

        List<Accommodation> listOfAccommodationsFoundFromFilteringWeather = new ArrayList<>();

        for (Accommodation accommodationsFoundFromFilteringSafety: listOfAccommodationsFoundFromFilteringSafety){
            String locationSKOfAccomodation = accommodationsFoundFromFilteringSafety.getAccommodationLocationSk();

            String convertedMonthSK = convertToMonthSK(month);

            WeatherOfAreaOfAccommodation weatherOfAreaOfAccommodation = weatherOfAreaOfAccommodationRepository.getWeatherDetailsOfLocation(locationSKOfAccomodation, convertedMonthSK);

            BigDecimal precipitation = (weatherOfAreaOfAccommodation != null && weatherOfAreaOfAccommodation.getAveragePrecipitation() != null)
                    ? weatherOfAreaOfAccommodation.getAveragePrecipitation()
                    : BigDecimal.ZERO;

            BigDecimal temperature = (weatherOfAreaOfAccommodation != null && weatherOfAreaOfAccommodation.getAverageTemperature() != null)
                    ? weatherOfAreaOfAccommodation.getAverageTemperature()
                    : BigDecimal.ZERO;

            int weatherPoints = 0;

// ---------- Precipitation scoring ----------
// < 80           => +10
// 80 .. 100      => +5
// 100 .. 120     => 0 (neutral)
// 120 .. 150     => -5
// >= 150         => -10
            BigDecimal P80  = new BigDecimal("80");
            BigDecimal P100 = new BigDecimal("100");
            BigDecimal P120 = new BigDecimal("120");
            BigDecimal P150 = new BigDecimal("150");

            if (precipitation.compareTo(P80) < 0) {
                weatherPoints += 10;
            } else if (precipitation.compareTo(P100) <= 0) { // 80..100
                weatherPoints += 5;
            } else if (precipitation.compareTo(P120) <= 0) { // 100..120
                // neutral (no change)
            } else if (precipitation.compareTo(P150) < 0) {  // 120..150
                weatherPoints -= 5;
            } else { // >= 150
                weatherPoints -= 10;
            }

// ---------- Temperature scoring ----------
// > 40          => -10
// 30 .. 40      => -5
// 26 .. 28      => +5
// 24 .. 26      => +10
// 22 .. 24      => +5
// 10 .. 20      => -5
// <= 10         => -10
            BigDecimal T10  = new BigDecimal("10");
            BigDecimal T20  = new BigDecimal("20");
            BigDecimal T22  = new BigDecimal("22");
            BigDecimal T24  = new BigDecimal("24");
            BigDecimal T26  = new BigDecimal("26");
            BigDecimal T28  = new BigDecimal("28");
            BigDecimal T30  = new BigDecimal("30");
            BigDecimal T40  = new BigDecimal("40");

            if (temperature.compareTo(T40) > 0) {              // > 40
                weatherPoints -= 10;
            } else if (temperature.compareTo(T30) >= 0) {      // 30 .. 40
                weatherPoints -= 5;
            } else if (temperature.compareTo(T26) >= 0 && temperature.compareTo(T28) <= 0) { // 26 .. 28
                weatherPoints += 5;
            } else if (temperature.compareTo(T24) >= 0 && temperature.compareTo(T26) < 0) {  // 24 .. <26
                weatherPoints += 10;
            } else if (temperature.compareTo(T22) >= 0 && temperature.compareTo(T24) < 0) {  // 22 .. <24
                weatherPoints += 5;
            } else if (temperature.compareTo(T10) >= 0 && temperature.compareTo(T20) <= 0) { // 10 .. 20
                weatherPoints -= 5;
            } else { // <= 10
                weatherPoints -= 10;
            }

            if(weatherPoints>5){
                listOfAccommodationsFoundFromFilteringWeather.add(accommodationsFoundFromFilteringSafety);
            }

        }

        List<AccomodationDTO> listOfFilteredAccommodations = new ArrayList<>();

        for(Accommodation accommodationsFoundFromFilteringWeather:listOfAccommodationsFoundFromFilteringWeather){

            String province = accommodationLocationRepository.getProvince(accommodationsFoundFromFilteringWeather.getAccommodationLocationSk());
            String district = accommodationLocationRepository.getDistrict(accommodationsFoundFromFilteringWeather.getAccommodationLocationSk());
            String city = accommodationLocationRepository.getCity(accommodationsFoundFromFilteringWeather.getAccommodationLocationSk());
            String address = String.format("%s, %s, %s province", city, district, province);


            listOfFilteredAccommodations.add(new AccomodationDTO(accommodationsFoundFromFilteringWeather.getAccommodationName(),address));
        }

        return listOfFilteredAccommodations;

    }


    public List<AccomodationDTO> search2(Integer month) {

        List<AccomodationDTO> listOfFilteredAccommodations = new ArrayList<>();

        return listOfFilteredAccommodations;

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

