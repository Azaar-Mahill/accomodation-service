package com.example.accomodation_service_backend.service;

import com.example.accomodation_service_backend.dto.*;
import com.example.accomodation_service_backend.model.*;
import com.example.accomodation_service_backend.repo.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.time.LocalDate;

@Service
public class AccommodationService {

    private final AccommodationTypeRepository accommodationTypeRepository;
    private final AccommodationLocationRepository accommodationLocationRepository;
    private final AccommodationRepository accommodationRepository;
    private final SafetyOfAreaOfAccommodationRepository safetyOfAreaOfAccommodationRepository;

    private final WeatherOfAreaOfAccommodationRepository weatherOfAreaOfAccommodationRepository;

    private final BookAccomodationRepository bookAccomodationRepository;

    private final RoomRepository roomRepository;

    private final BigDecimal weightOfAccidents = BigDecimal.valueOf(2);

    private final BigDecimal weightOfCrimes = BigDecimal.valueOf(1);

    private final BigDecimal safetyThreshold = BigDecimal.valueOf(2);

    public AccommodationService(
            AccommodationTypeRepository accommodationTypeRepository,
            AccommodationLocationRepository accommodationLocationRepository,
            AccommodationRepository accommodationRepository,
            SafetyOfAreaOfAccommodationRepository safetyOfAreaOfAccommodationRepository,
            WeatherOfAreaOfAccommodationRepository weatherOfAreaOfAccommodationRepository,
            RoomRepository roomRepository,
            BookAccomodationRepository bookAccomodationRepository
    ) {
        this.accommodationTypeRepository = accommodationTypeRepository;
        this.accommodationLocationRepository = accommodationLocationRepository;
        this.accommodationRepository = accommodationRepository;
        this.safetyOfAreaOfAccommodationRepository = safetyOfAreaOfAccommodationRepository;
        this.weatherOfAreaOfAccommodationRepository = weatherOfAreaOfAccommodationRepository;
        this.roomRepository = roomRepository;
        this.bookAccomodationRepository = bookAccomodationRepository;
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
//  > 40          => -10
//  35 .. 40      => -5
//  32 .. 35      => 0
//  28 .. 32      => +5
//  23 .. 28      => +10
//  19 .. 23      => +5
//  15 .. 19      => 0
//  10 .. 15      => -5
//  < 10          => -10

            BigDecimal T10  = new BigDecimal("10");
            BigDecimal T15  = new BigDecimal("15");
            BigDecimal T19  = new BigDecimal("19");
            BigDecimal T23  = new BigDecimal("23");
            BigDecimal T28  = new BigDecimal("28");
            BigDecimal T32  = new BigDecimal("32");
            BigDecimal T35  = new BigDecimal("35");
            BigDecimal T40  = new BigDecimal("40");


            if (temperature.compareTo(T40) > 0) {                                         // > 40
                weatherPoints -= 10;
            } else if (temperature.compareTo(T35) >= 0 && temperature.compareTo(T40) <= 0) {  // 35 .. 40
                weatherPoints -= 5;
            } else if (temperature.compareTo(T32) >= 0 && temperature.compareTo(T35) < 0) {   // 32 .. <35
                // 0 points (no change)
            } else if (temperature.compareTo(T28) >= 0 && temperature.compareTo(T32) < 0) {   // 28 .. <32
                weatherPoints += 5;
            } else if (temperature.compareTo(T23) >= 0 && temperature.compareTo(T28) < 0) {   // 23 .. <28
                weatherPoints += 10;
            } else if (temperature.compareTo(T19) >= 0 && temperature.compareTo(T23) < 0) {   // 19 .. <23
                weatherPoints += 5;
            } else if (temperature.compareTo(T15) >= 0 && temperature.compareTo(T19) < 0) {   // 15 .. <19
                // 0 points (no change)
            } else if (temperature.compareTo(T10) >= 0 && temperature.compareTo(T15) < 0) {   // 10 .. <15
                weatherPoints -= 5;
            } else {                                                                   // < 10
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

            AccomodationDTO accomodationDTO = new AccomodationDTO();
            accomodationDTO.setId(accommodationsFoundFromFilteringWeather.getAccommodationSk());
            accomodationDTO.setAccommodationName(accommodationsFoundFromFilteringWeather.getAccommodationName());
            accomodationDTO.setAccommodationAddress(address);

            listOfFilteredAccommodations.add(accomodationDTO);
        }

        return listOfFilteredAccommodations;

    }


    public List<AccomodationWeatherDTO> search2(Integer month) {

        String convertedMonthSK = convertToMonthSK(month);

        List<WeatherOfAreaOfAccommodation> listOfFilteredWeatherDetailsBasedOnMonth = weatherOfAreaOfAccommodationRepository.getWeatherDetailsOfLocationBasedOnMonth(convertedMonthSK);

        List<WeatherOfAreaOfAccommodation2> listOfFilteredWeatherDetailsBasedOnWeather = new ArrayList<>();

        for(WeatherOfAreaOfAccommodation weatherDetailsBasedOnMonth:listOfFilteredWeatherDetailsBasedOnMonth){

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

            if ((weatherDetailsBasedOnMonth.getAveragePrecipitation()).compareTo(P80) < 0) {
                weatherPoints += 10;
            } else if ((weatherDetailsBasedOnMonth.getAveragePrecipitation()).compareTo(P100) <= 0) { // 80..100
                weatherPoints += 5;
            } else if ((weatherDetailsBasedOnMonth.getAveragePrecipitation()).compareTo(P120) <= 0) { // 100..120
                // neutral (no change)
            } else if ((weatherDetailsBasedOnMonth.getAveragePrecipitation()).compareTo(P150) < 0) {  // 120..150
                weatherPoints -= 5;
            } else { // >= 150
                weatherPoints -= 10;
            }

// ---------- Temperature scoring ----------
//  > 40          => -10
//  35 .. 40      => -5
//  32 .. 35      => 0
//  28 .. 32      => +5
//  23 .. 28      => +10
//  19 .. 23      => +5
//  15 .. 19      => 0
//  10 .. 15      => -5
//  < 10          => -10

            BigDecimal T10  = new BigDecimal("10");
            BigDecimal T15  = new BigDecimal("15");
            BigDecimal T19  = new BigDecimal("19");
            BigDecimal T23  = new BigDecimal("23");
            BigDecimal T28  = new BigDecimal("28");
            BigDecimal T32  = new BigDecimal("32");
            BigDecimal T35  = new BigDecimal("35");
            BigDecimal T40  = new BigDecimal("40");

            BigDecimal avgTemp = weatherDetailsBasedOnMonth.getAverageTemperature();

            if (avgTemp.compareTo(T40) > 0) {                                         // > 40
                weatherPoints -= 10;
            } else if (avgTemp.compareTo(T35) >= 0 && avgTemp.compareTo(T40) <= 0) {  // 35 .. 40
                weatherPoints -= 5;
            } else if (avgTemp.compareTo(T32) >= 0 && avgTemp.compareTo(T35) < 0) {   // 32 .. <35
                // 0 points (no change)
            } else if (avgTemp.compareTo(T28) >= 0 && avgTemp.compareTo(T32) < 0) {   // 28 .. <32
                weatherPoints += 5;
            } else if (avgTemp.compareTo(T23) >= 0 && avgTemp.compareTo(T28) < 0) {   // 23 .. <28
                weatherPoints += 10;
            } else if (avgTemp.compareTo(T19) >= 0 && avgTemp.compareTo(T23) < 0) {   // 19 .. <23
                weatherPoints += 5;
            } else if (avgTemp.compareTo(T15) >= 0 && avgTemp.compareTo(T19) < 0) {   // 15 .. <19
                // 0 points (no change)
            } else if (avgTemp.compareTo(T10) >= 0 && avgTemp.compareTo(T15) < 0) {   // 10 .. <15
                weatherPoints -= 5;
            } else {                                                                   // < 10
                weatherPoints -= 10;
            }


            String weatherStatus;

            if(weatherPoints>10){
                weatherStatus = "Super";
            } else if (weatherPoints<5) {
                weatherStatus = "Bad";
            }else {
                weatherStatus = "Normal";
            }

            //////////////
            SafetyOfAreaOfAccommodation safetyOfAreaOfAccommodation = safetyOfAreaOfAccommodationRepository.getSafetyDetailsOfLocation(weatherDetailsBasedOnMonth.getAccommodationLocationSk(), convertedMonthSK);

            BigDecimal accident = (safetyOfAreaOfAccommodation != null && safetyOfAreaOfAccommodation.getAccidentRate() != null)
                    ? safetyOfAreaOfAccommodation.getAccidentRate()
                    : BigDecimal.ZERO;

            BigDecimal crime = (safetyOfAreaOfAccommodation != null && safetyOfAreaOfAccommodation.getCrimeRate() != null)
                    ? safetyOfAreaOfAccommodation.getCrimeRate()
                    : BigDecimal.ZERO;

            BigDecimal safetyRateOfAccommodation = (weightOfAccidents.multiply(accident)).add(weightOfCrimes.multiply(crime));

            String safetyStatus;

            BigDecimal ONE  = new BigDecimal("1.0");
            BigDecimal TWO  = new BigDecimal("2.0");

// safetyRate <= 1  -> Super
// 1 < safetyRate <= 2 -> Normal
// safetyRate > 2  -> Bad
            if (safetyRateOfAccommodation.compareTo(ONE) <= 0) {
                safetyStatus = "Super";
            } else if (safetyRateOfAccommodation.compareTo(TWO) <= 0) {
                safetyStatus = "Normal";
            } else {
                safetyStatus = "Bad";
            }

            //////////////

            WeatherOfAreaOfAccommodation2 weatherOfAreaOfAccommodation2 = new WeatherOfAreaOfAccommodation2();

            weatherOfAreaOfAccommodation2.setWeatherOfAreaOfAccommodationSk(weatherDetailsBasedOnMonth.getWeatherOfAreaOfAccommodationSk());
            weatherOfAreaOfAccommodation2.setWeatherOfAreaOfAccommodationCode(weatherDetailsBasedOnMonth.getWeatherOfAreaOfAccommodationCode());
            weatherOfAreaOfAccommodation2.setMonthSk(weatherDetailsBasedOnMonth.getMonthSk());
            weatherOfAreaOfAccommodation2.setAverageTemperature(weatherDetailsBasedOnMonth.getAverageTemperature());
            weatherOfAreaOfAccommodation2.setAveragePrecipitation(weatherDetailsBasedOnMonth.getAveragePrecipitation());
            weatherOfAreaOfAccommodation2.setAccommodationLocationSk((weatherDetailsBasedOnMonth.getAccommodationLocationSk()));
            weatherOfAreaOfAccommodation2.setWeatherStatus(weatherStatus);
            weatherOfAreaOfAccommodation2.setCrimeRate(crime);
            weatherOfAreaOfAccommodation2.setAccidentRate(accident);
            weatherOfAreaOfAccommodation2.setSafetyStatus(safetyStatus);

            listOfFilteredWeatherDetailsBasedOnWeather.add(weatherOfAreaOfAccommodation2);

        }

        List<AccomodationWeatherDTO> listOfFilteredAccommodations = new ArrayList<>();

        for(WeatherOfAreaOfAccommodation2 filteredWeatherDetailsBasedOnWeather:listOfFilteredWeatherDetailsBasedOnWeather){

            AccomodationWeatherDTO accomodationWeatherDTO = new AccomodationWeatherDTO();
            accomodationWeatherDTO.setTemperature(String.valueOf(filteredWeatherDetailsBasedOnWeather.getAverageTemperature()));
            accomodationWeatherDTO.setPrecipitation(String.valueOf(filteredWeatherDetailsBasedOnWeather.getAveragePrecipitation()));

            String accommodationLocationSk = filteredWeatherDetailsBasedOnWeather.getAccommodationLocationSk();
            List<Accommodation> filteredAccommodationsUsingLocationSk = accommodationRepository.getAccomodationsFromAccommodationLocationSk(accommodationLocationSk);
            accomodationWeatherDTO.setId(filteredAccommodationsUsingLocationSk.get(0).getAccommodationSk());
            accomodationWeatherDTO.setAccommodationName(filteredAccommodationsUsingLocationSk.get(0).getAccommodationName());
            String convertedMonthSK2 = convertToMonthSK(month);

            String province = accommodationLocationRepository.getProvince(accommodationLocationSk);
            String district = accommodationLocationRepository.getDistrict(accommodationLocationSk);
            String city = accommodationLocationRepository.getCity(accommodationLocationSk);
            String address = String.format("%s, %s, %s province", city, district, province);
            accomodationWeatherDTO.setAccommodationAddress(address);

            accomodationWeatherDTO.setWeatherStatus(filteredWeatherDetailsBasedOnWeather.getWeatherStatus());
            accomodationWeatherDTO.setEnvironment(accommodationLocationRepository.getEnvironment(accommodationLocationSk));

            accomodationWeatherDTO.setAccidentRate(filteredWeatherDetailsBasedOnWeather.getAccidentRate());
            accomodationWeatherDTO.setCrimeRate(filteredWeatherDetailsBasedOnWeather.getCrimeRate());
            accomodationWeatherDTO.setSafetyStatus(filteredWeatherDetailsBasedOnWeather.getSafetyStatus());

            listOfFilteredAccommodations.add(accomodationWeatherDTO);

        }

        return listOfFilteredAccommodations;

    }
    public String convertToMonthSK(Integer month) {
        if(month>9){
            return "2025-"+month;
        }else{
            return "2025-0"+month;
        }

    }

    public List<AccomodationTypeDTO> search3(String accomodationType) {

        List<String> listOfAccommodationTypeSks;

        if ("Any".equalsIgnoreCase(accomodationType)) {
            listOfAccommodationTypeSks = accommodationTypeRepository.findAllAccommodationTypeSks();
        } else {
            listOfAccommodationTypeSks = accommodationTypeRepository.findAccommodationTypeSksByTypeName(accomodationType);
        }

        List<Accommodation> listOfAccommodations = new ArrayList<>();

        for(String accommodationTypeSk: listOfAccommodationTypeSks){
            List<Accommodation> AccommodationsWithSameType = accommodationRepository.getAllAccomodationsFromTypeSK(accommodationTypeSk);

            for(Accommodation accommodationWithSameType: AccommodationsWithSameType) {
                listOfAccommodations.add(accommodationWithSameType);
            }

        }

        List<AccomodationTypeDTO> listOfAccomodationTypeDTO = new ArrayList<>();
        
        for(Accommodation accommodation: listOfAccommodations){
            AccomodationTypeDTO accomodationTypeDTO = new AccomodationTypeDTO();
            accomodationTypeDTO.setId(accommodation.getAccommodationSk());
            accomodationTypeDTO.setAccommodationName(accommodation.getAccommodationName());
            String accommodationLocationSk = accommodation.getAccommodationLocationSk();
            String Environment = accommodationLocationRepository.getEnvironment(accommodationLocationSk);
            accomodationTypeDTO.setEnvironment(Environment);

            String province = accommodationLocationRepository.getProvince(accommodationLocationSk);
            String district = accommodationLocationRepository.getDistrict(accommodationLocationSk);
            String city = accommodationLocationRepository.getCity(accommodationLocationSk);
            String address = String.format("%s, %s, %s province", city, district, province);
            accomodationTypeDTO.setAccommodationAddress(address);

            List<WeatherOfAreaOfAccommodation> listOfWeatherOfAreaOfAccommodation = weatherOfAreaOfAccommodationRepository.getAllWeatherDetailsOfLocation(accommodationLocationSk);

            Map<Integer, BigDecimal> avgTempByMonthC = new HashMap<>();
            Map<Integer, BigDecimal> avgPrecipByMonthMm = new HashMap<>();

            for (WeatherOfAreaOfAccommodation weatherOfAreaOfAccommodation : listOfWeatherOfAreaOfAccommodation) {

                String monthSk = weatherOfAreaOfAccommodation.getMonthSk();

                // safety check – avoid StringIndexOutOfBoundsException
                if (monthSk == null || monthSk.length() < 7) {
                    continue; // or handle error
                }

                // last two characters are the month: positions 5 and 6 (0-based)
                int month = Integer.parseInt(monthSk.substring(5, 7));
                // e.g. "2024-01" -> "01" -> 1
                //      "2024-10" -> "10" -> 10

                avgTempByMonthC.put(month, weatherOfAreaOfAccommodation.getAverageTemperature());
                avgPrecipByMonthMm.put(month, weatherOfAreaOfAccommodation.getAveragePrecipitation());

            }
            accomodationTypeDTO.setAvgTempByMonthC(avgTempByMonthC);
            accomodationTypeDTO.setAvgPrecipByMonthMm(avgPrecipByMonthMm);

            List<SafetyOfAreaOfAccommodation> listOfSafetyOfAreaOfAccommodation = safetyOfAreaOfAccommodationRepository.getSafetyDetailsOfLocationUsingLocationSKOfAccomodation(accommodationLocationSk);

            Map<Integer, BigDecimal> avgCrimeRateByMonth = new HashMap<>();
            Map<Integer, BigDecimal> avgAccidentRateByMonth = new HashMap<>();

            for (SafetyOfAreaOfAccommodation safetyOfAreaOfAccommodation : listOfSafetyOfAreaOfAccommodation) {

                String monthSk = safetyOfAreaOfAccommodation.getMonthSk();

                // safety check – avoid StringIndexOutOfBoundsException
                if (monthSk == null || monthSk.length() < 7) {
                    continue; // or handle error
                }

                // last two characters are the month: positions 5 and 6 (0-based)
                int month = Integer.parseInt(monthSk.substring(5, 7));
                // e.g. "2024-01" -> "01" -> 1
                //      "2024-10" -> "10" -> 10

                avgCrimeRateByMonth.put(month, safetyOfAreaOfAccommodation.getCrimeRate());
                avgAccidentRateByMonth.put(month, safetyOfAreaOfAccommodation.getAccidentRate());

            }
            accomodationTypeDTO.setAvgCrimeRateByMonth(avgCrimeRateByMonth);
            accomodationTypeDTO.setAvgAccidentRateByMonth(avgAccidentRateByMonth);

            listOfAccomodationTypeDTO.add(accomodationTypeDTO);


        }

        return listOfAccomodationTypeDTO;

    }

    public AccomodationByIdDTO findAccomodationById(String accomodationSK) {

        AccomodationByIdDTO accomodationByIdDTO = new AccomodationByIdDTO();

        Accommodation accomodationById = accommodationRepository.findAccomodationById(accomodationSK);

        if(accomodationById == null){
            return null;
        }

        accomodationByIdDTO.setId(accomodationById.getAccommodationSk());
        accomodationByIdDTO.setAccommodationName(accomodationById.getAccommodationName());

        String accommodationLocationSk = accomodationById.getAccommodationLocationSk();
        String province = accommodationLocationRepository.getProvince(accommodationLocationSk);
        String district = accommodationLocationRepository.getDistrict(accommodationLocationSk);
        String city = accommodationLocationRepository.getCity(accommodationLocationSk);
        String address = String.format("%s, %s, %s province", city, district, province);

        accomodationByIdDTO.setAccommodationAddress(address);

        String accommodationTypeSk = accomodationById.getAccommodationTypeSk();
        String accommodationType = accommodationTypeRepository.findTypeBySK(accommodationTypeSk);

        accomodationByIdDTO.setAccommodationType(accommodationType);

        String environment = accommodationLocationRepository.getEnvironment(accommodationLocationSk);

        accomodationByIdDTO.setEnvironment(environment);

        return accomodationByIdDTO;

    }

    public List<AccomodationKPIDTO> findKPIInformation(String province, String useDistrict,String district,String useCity,String city) {

        List<String> accomodationLocationSks = new ArrayList<>();

        boolean useDistrictFlag = "true".equalsIgnoreCase(useDistrict);
        boolean useCityFlag = "true".equalsIgnoreCase(useCity);

        if (useCityFlag && district != null && city != null) {
            // province + district + city
            accomodationLocationSks = accommodationLocationRepository.findAccommodationLocationSksByProvinceAndDistrictCity(province, district, city);

        } else if (useDistrictFlag && district != null) {
            // province + district only
            accomodationLocationSks = accommodationLocationRepository.findAccommodationLocationSksByProvinceAndDistrict(province, district);

        } else {
            // province only
            accomodationLocationSks = accommodationLocationRepository.findAccommodationLocationSksByProvince(province);
        }

        List<Accommodation> listOfFilteredAccomodationsUsingAccomodationLocationSk = new ArrayList<>();

        for(String accomodationLocationSk:accomodationLocationSks){
            listOfFilteredAccomodationsUsingAccomodationLocationSk.addAll(accommodationRepository.getAccomodationsFromAccommodationLocationSk(accomodationLocationSk));
        }

        List<AccomodationKPIDTO> listOfAccomodationKPIDTO = new ArrayList<>();
        int currentMonthNumber = LocalDate.now().getMonthValue();
        int currentYearNumber = LocalDate.now().getYear();

        for(Accommodation accomodationsUsingAccomodationLocationSk:listOfFilteredAccomodationsUsingAccomodationLocationSk){
            AccomodationKPIDTO accomodationKPIDTO = new AccomodationKPIDTO();
            accomodationKPIDTO.setId(accomodationsUsingAccomodationLocationSk.getAccommodationSk());
            accomodationKPIDTO.setAccommodationName(accomodationsUsingAccomodationLocationSk.getAccommodationName());

            String provinceForAddress = accommodationLocationRepository.getProvince(accomodationsUsingAccomodationLocationSk.getAccommodationLocationSk());
            String districtForAddress = accommodationLocationRepository.getDistrict(accomodationsUsingAccomodationLocationSk.getAccommodationLocationSk());
            String cityForAddress = accommodationLocationRepository.getCity(accomodationsUsingAccomodationLocationSk.getAccommodationLocationSk());
            String address = String.format("%s, %s, %s province", cityForAddress, districtForAddress, provinceForAddress);

            accomodationKPIDTO.setAccommodationAddress(address);
            accomodationKPIDTO.setEnvironment(accommodationLocationRepository.getEnvironment(accomodationsUsingAccomodationLocationSk.getAccommodationLocationSk()));

            List<String> listOfFilteredRoomSksUsingAccomodationSk = new ArrayList<>();
            listOfFilteredRoomSksUsingAccomodationSk = roomRepository.getRoomsUsingAccomodationSk(accomodationsUsingAccomodationLocationSk.getAccommodationSk());

            List<BookAccomodation> listOfBookings = new ArrayList<>();
            for(String filteredRoomSksUsingAccomodationSk: listOfFilteredRoomSksUsingAccomodationSk){
                listOfBookings.addAll(bookAccomodationRepository.getBookingsUsingRoomSk(filteredRoomSksUsingAccomodationSk));
            }

            Map<Integer, Integer> bookingsByMonth = new HashMap<>();
            Map<Integer, BigDecimal> revenueByMonth = new HashMap<>();
            Map<Integer, BigDecimal> averageDailyRate = new HashMap<>();
            Map<Integer, BigDecimal> revenuePerAvailableRoom = new HashMap<>();
            Map<Integer, BigDecimal> averageLengthOfStay = new HashMap<>();

            for (int month = 1; month <= currentMonthNumber; month++) {

                int totalMonthBookings = 0;
                BigDecimal totalMonthRevenue = BigDecimal.ZERO;
                int totalNumberOfRooms = 0;
                List<String> roomSks = new ArrayList<>();
                BigDecimal averageDailyRatePerMonth = BigDecimal.ZERO;
                BigDecimal revenuePerAvailableRoomPerMonth = BigDecimal.ZERO;
                int totalNumberOfDaysStayed = 0;
                BigDecimal averageLengthOfStayPerMonth = BigDecimal.ZERO;

                for (BookAccomodation booking : listOfBookings) {

                    int bookingYear = Integer.parseInt(booking.getCheckinDateSk().substring(0, 4));

                    if(currentYearNumber != bookingYear){
                        continue;
                    }
                    int bookingMonth = Integer.parseInt(booking.getCheckinDateSk().substring(5, 7));

                    if (month == bookingMonth) {

                        totalMonthBookings++;

                        // SAFE handling of null BigDecimal
                        BigDecimal amount = booking.getTotalAmount() == null
                                ? BigDecimal.ZERO
                                : booking.getTotalAmount();

                        totalMonthRevenue = totalMonthRevenue.add(amount);
                    }

                    if(!roomSks.contains(booking.getRoomSk())){
                        roomSks.add(booking.getRoomSk());
                        totalNumberOfRooms = totalNumberOfRooms + 1;
                    }

                    String checkinDate= booking.getCheckinDateSk();
                    String checkoutDate= booking.getCheckoutDateSk();

                    LocalDate checkin = LocalDate.parse(checkinDate);
                    LocalDate checkout = LocalDate.parse(checkoutDate);

                    totalNumberOfDaysStayed = (int) (totalNumberOfDaysStayed + ChronoUnit.DAYS.between(checkin, checkout)+1);
                }

                if (totalNumberOfRooms > 0) {
                    averageDailyRatePerMonth = totalMonthRevenue.divide(
                            BigDecimal.valueOf(totalNumberOfRooms),
                            2,                              // scale (2 decimal places)
                            RoundingMode.HALF_UP            // rounding mode
                    );
                } else {
                    averageDailyRatePerMonth = BigDecimal.ZERO;
                }

                if (totalMonthBookings > 0) {
                    revenuePerAvailableRoomPerMonth = totalMonthRevenue.divide(
                            BigDecimal.valueOf(totalMonthBookings),
                            2,                              // scale (2 decimal places)
                            RoundingMode.HALF_UP            // rounding mode
                    );
                    averageLengthOfStayPerMonth = BigDecimal.valueOf(totalNumberOfDaysStayed/totalMonthBookings);

                } else {
                    revenuePerAvailableRoomPerMonth = BigDecimal.ZERO;
                    averageLengthOfStayPerMonth = BigDecimal.ZERO;
                }

                bookingsByMonth.put(month, totalMonthBookings);
                revenueByMonth.put(month, totalMonthRevenue);   // <-- MUST be added
                averageDailyRate.put(month, averageDailyRatePerMonth);
                revenuePerAvailableRoom.put(month, revenuePerAvailableRoomPerMonth);
                averageLengthOfStay.put(month, averageLengthOfStayPerMonth);
            }

            accomodationKPIDTO.setBookingsByMonth(bookingsByMonth);
            accomodationKPIDTO.setRevenueByMonth(revenueByMonth);
            accomodationKPIDTO.setAverageDailyRate(averageDailyRate);
            accomodationKPIDTO.setRevenuePerAvailableRoom(revenuePerAvailableRoom);
            accomodationKPIDTO.setAverageLengthOfStay(averageLengthOfStay);

            listOfAccomodationKPIDTO.add(accomodationKPIDTO);

        }

        return listOfAccomodationKPIDTO;

    }

    public List<AccomodationDTO> getAllAcommodations(){

        List<AccomodationDTO> returnAllAcommodations = new ArrayList<>();

        List<Accommodation> listOfAllAccomodations = accommodationRepository.getAllAcommodations();

        for(Accommodation allAccomodation: listOfAllAccomodations){

            AccomodationDTO accomodationDTO = new AccomodationDTO();
            accomodationDTO.setId(allAccomodation.getAccommodationSk());
            accomodationDTO.setAccommodationName(allAccomodation.getAccommodationName());

            String province = accommodationLocationRepository.getProvince(allAccomodation.getAccommodationLocationSk());
            String district = accommodationLocationRepository.getDistrict(allAccomodation.getAccommodationLocationSk());
            String city = accommodationLocationRepository.getCity(allAccomodation.getAccommodationLocationSk());
            String address = String.format("%s, %s, %s province", city, district, province);

            accomodationDTO.setAccommodationAddress(address);
            returnAllAcommodations.add(accomodationDTO);

        }

        return returnAllAcommodations;

    }

    public ForecastingDTO forecast(String accommodationID) {

        ForecastingDTO forecastingDTO = new ForecastingDTO();
        Accommodation requireAccommodation = accommodationRepository.findAccomodationById(accommodationID);

        forecastingDTO.setId(requireAccommodation.getAccommodationSk());
        forecastingDTO.setAccommodationName(requireAccommodation.getAccommodationName());

        String province = accommodationLocationRepository.getProvince(requireAccommodation.getAccommodationLocationSk());
        String district = accommodationLocationRepository.getDistrict(requireAccommodation.getAccommodationLocationSk());
        String city = accommodationLocationRepository.getCity(requireAccommodation.getAccommodationLocationSk());
        String address = String.format("%s, %s, %s province", city, district, province);

        forecastingDTO.setAccommodationAddress(address);
        forecastingDTO.setEnvironment(accommodationLocationRepository.getEnvironment(requireAccommodation.getAccommodationLocationSk()));

        List<String> roomSKs = roomRepository.getRoomsUsingAccomodationSk(accommodationID);
        List<BookAccomodation> bookingsFromRoomSK = new ArrayList<>();
        for (String roomSK : roomSKs) {
            bookingsFromRoomSK.addAll(bookAccomodationRepository.getBookingsUsingRoomSk(roomSK));
        }

        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();

        // ============================================================================================
        // 1. TAKE BOOKINGS FOR PAST 10 MONTHS (x = 1 to 10)
        // ============================================================================================
        Map<Integer, Integer> past10MonthsCount = new LinkedHashMap<>();
        Map<Integer, BigDecimal> past10MonthsRevenue = new LinkedHashMap<>();

        for (int i = 10; i >= 1; i--) {
            LocalDate targetMonth = today.minusMonths(i);
            int targetYear = targetMonth.getYear();
            int targetMonthNum = targetMonth.getMonthValue();

            int count = 0;
            BigDecimal MonthRevenue = BigDecimal.ZERO;
            for (BookAccomodation b : bookingsFromRoomSK) {
                int year = Integer.parseInt(b.getCheckinDateSk().substring(0, 4));
                int month = Integer.parseInt(b.getCheckinDateSk().substring(5, 7));
                if (year == targetYear && month == targetMonthNum) {
                    count++;
                    MonthRevenue = MonthRevenue.add(b.getTotalAmount());
                }
            }
            past10MonthsCount.put(11 - i, count);   // x = 1 → oldest, x = 10 → latest
            past10MonthsRevenue.put(11 - i, MonthRevenue);
        }

        // ============================================================================================
        // 2. COMPUTE LINEAR REGRESSION (BigDecimal)
        // ============================================================================================

        BigDecimal sumX = BigDecimal.ZERO;
        BigDecimal sumY = BigDecimal.ZERO;
        BigDecimal sumXY = BigDecimal.ZERO;
        BigDecimal sumXX = BigDecimal.ZERO;

        for (Map.Entry<Integer, Integer> entry : past10MonthsCount.entrySet()) {
            BigDecimal x = new BigDecimal(entry.getKey());
            BigDecimal y = new BigDecimal(entry.getValue());

            sumX = sumX.add(x);
            sumY = sumY.add(y);
            sumXY = sumXY.add(x.multiply(y));
            sumXX = sumXX.add(x.multiply(x));
        }

        BigDecimal n = new BigDecimal(past10MonthsCount.size());

        // slope b = (n Σxy - Σx Σy) / (n Σx^2 - (Σx)^2)
        BigDecimal numerator = (n.multiply(sumXY)).subtract(sumX.multiply(sumY));
        BigDecimal denominator = (n.multiply(sumXX)).subtract(sumX.multiply(sumX));

        BigDecimal b = numerator.divide(denominator, 8, RoundingMode.HALF_UP);
        BigDecimal a = (sumY.subtract(b.multiply(sumX))).divide(n, 8, RoundingMode.HALF_UP);

        ////////////////////////////////

        BigDecimal sumXRevenue = BigDecimal.ZERO;
        BigDecimal sumYRevenue = BigDecimal.ZERO;
        BigDecimal sumXYRevenue = BigDecimal.ZERO;
        BigDecimal sumXXRevenue = BigDecimal.ZERO;

        for (Map.Entry<Integer, BigDecimal> entry : past10MonthsRevenue.entrySet()) {
            BigDecimal x = new BigDecimal(entry.getKey());
            BigDecimal y = new BigDecimal(String.valueOf(entry.getValue()));

            sumXRevenue = sumXRevenue.add(x);
            sumYRevenue = sumYRevenue.add(y);
            sumXYRevenue = sumXYRevenue.add(x.multiply(y));
            sumXXRevenue = sumXXRevenue.add(x.multiply(x));
        }

        BigDecimal nRevenue = new BigDecimal(past10MonthsRevenue.size());

        // slope b = (n Σxy - Σx Σy) / (n Σx^2 - (Σx)^2)
        BigDecimal numeratorRevenue = (nRevenue.multiply(sumXYRevenue)).subtract(sumXRevenue.multiply(sumYRevenue));
        BigDecimal denominatorRevenue = (nRevenue.multiply(sumXXRevenue)).subtract(sumXRevenue.multiply(sumXRevenue));

        BigDecimal bRevenue = numeratorRevenue.divide(denominatorRevenue, 8, RoundingMode.HALF_UP);
        BigDecimal aRevenue = (sumYRevenue.subtract(bRevenue.multiply(sumXRevenue))).divide(nRevenue, 8, RoundingMode.HALF_UP);

        // ============================================================================================
        // 3. FORECAST NEXT 5 MONTHS USING REGRESSION
        // ============================================================================================

        Map<Integer, BigDecimal> regressionForecast = new LinkedHashMap<>();
        Map<Integer, BigDecimal> regressionForecastRevenue = new LinkedHashMap<>();

        for (int i = 1; i <= 5; i++) {
            BigDecimal x = new BigDecimal(10 + i);  // x = 11,12,13,14,15
            BigDecimal yHat = a.add(b.multiply(x));
            if (yHat.compareTo(BigDecimal.ZERO) < 0) yHat = BigDecimal.ZERO; // avoid negatives
            regressionForecast.put(i, yHat);

            BigDecimal xRevenue = new BigDecimal(10 + i);  // x = 11,12,13,14,15
            BigDecimal yHatRevenue = aRevenue.add(bRevenue.multiply(xRevenue));
            if (yHatRevenue.compareTo(BigDecimal.ZERO) < 0) yHatRevenue = BigDecimal.ZERO; // avoid negatives
            regressionForecastRevenue.put(i, yHatRevenue);
        }

        // ============================================================================================
        // 4. CALCULATE SEASONAL INDEX FOR EACH MONTH (BASED ON PAST YEARS)
        // ============================================================================================

        Map<Integer, BigDecimal> seasonalIndex = new HashMap<>();
        Map<Integer, BigDecimal> seasonalIndexRevenue = new HashMap<>();

        for (int month = 1; month <= 12; month++) {
            int count = 0;
            BigDecimal Revenue = BigDecimal.ZERO;
            int distinctYears = 0;
            Set<Integer> yearSet = new HashSet<>();

            for (BookAccomodation c : bookingsFromRoomSK) {
                int y = Integer.parseInt(c.getCheckinDateSk().substring(0, 4));
                int m = Integer.parseInt(c.getCheckinDateSk().substring(5, 7));

                if (m == month && y != currentYear) {
                    count++;
                    Revenue = Revenue.add(c.getTotalAmount());
                    yearSet.add(y);
                }
            }

            distinctYears = yearSet.size();
            BigDecimal index = (distinctYears == 0)
                    ? BigDecimal.ONE
                    : new BigDecimal(count).divide(new BigDecimal(distinctYears), 8, RoundingMode.HALF_UP);
            BigDecimal indexRevenue = (distinctYears == 0)
                    ? BigDecimal.ONE
                    : new BigDecimal(String.valueOf(Revenue)).divide(new BigDecimal(distinctYears), 8, RoundingMode.HALF_UP);

            seasonalIndex.put(month, index);
            seasonalIndexRevenue.put(month, indexRevenue);
        }

        // ============================================================================================
        // 5. FINAL FORECAST = regression forecast × seasonal index
        // ============================================================================================

        Map<Integer, Integer> finalForecastBookings = new LinkedHashMap<>();
        Map<Integer, BigDecimal> finalForecastRevenues = new LinkedHashMap<>();

        for (int i = 1; i <= 5; i++) {

            // 🔹 This is your "time index" or "extended month" (11, 12, 13, ...)
            int forecastKey = currentMonth + i;

            // 🔹 Convert to 1..12 ONLY for seasonal index lookup
            int monthOfYear = ((currentMonth - 1 + i) % 12) + 1;

            BigDecimal regValue = regressionForecast.get(i);
            BigDecimal regValueRevenue = regressionForecastRevenue.get(i);
            BigDecimal si = seasonalIndex.get(monthOfYear);
            BigDecimal siRevenue = seasonalIndexRevenue.get(monthOfYear);
            if (si == null) {
                si = BigDecimal.ONE; // safety fallback
            }

            if (siRevenue == null) {
                siRevenue = BigDecimal.ONE; // safety fallback
            }

            int finalValueInt = regValue
                    .multiply(si)
                    .setScale(0, RoundingMode.HALF_UP)
                    .intValue();

            BigDecimal finalRevenueValue = regValueRevenue
                    .multiply(siRevenue)
                    .setScale(0, RoundingMode.HALF_UP);

            // 🔹 Store using the original, non-wrapped key (e.g., 13, 14, 15)
            finalForecastBookings.put(forecastKey, finalValueInt);
            finalForecastRevenues.put(forecastKey,finalRevenueValue);
        }

        forecastingDTO.setForecastBookings(finalForecastBookings);
        forecastingDTO.setForecastRevenues(finalForecastRevenues);

        BigDecimal avgDiscountPerMonth = BigDecimal.ZERO;
        BigDecimal avgRoomRatePerMonth = BigDecimal.ZERO;

        YearMonth currentYm = YearMonth.now();
        YearMonth startYm = currentYm.minusMonths(4);

        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal totalPerNightRate = BigDecimal.ZERO;
        int count = 0;
        int countForNightRate = 0;

        for (BookAccomodation booking : bookingsFromRoomSK) {

            if (booking.getDiscount() == null) {
                continue;
            }

            LocalDate checkinDate = LocalDate.parse(booking.getCheckinDateSk());
            YearMonth bookingYm = YearMonth.from(checkinDate);

            if (bookingYm.isBefore(startYm) || bookingYm.isAfter(currentYm)) {
                continue;
            }

            totalDiscount = totalDiscount.add(booking.getDiscount());
            count++;
        }

        for (BookAccomodation booking : bookingsFromRoomSK) {

            if (booking.getTotalAmount() == null) {
                // no total amount recorded – skip
                continue;
            }

            // Parse dates (format: "YYYY-MM-DD")
            LocalDate checkinDate = LocalDate.parse(booking.getCheckinDateSk());
            LocalDate checkoutDate = LocalDate.parse(booking.getCheckoutDateSk());

            YearMonth bookingYm = YearMonth.from(checkinDate);

            // Only consider bookings in the last 5 months (based on check-in month)
            if (bookingYm.isBefore(startYm) || bookingYm.isAfter(currentYm)) {
                continue;
            }

            long nights = ChronoUnit.DAYS.between(checkinDate, checkoutDate);
            if (nights <= 0) {
                // invalid or same-day booking – skip
                continue;
            }

            // per-night room rate = TotalAmount / nights
            BigDecimal perNightRate = booking.getTotalAmount()
                    .divide(BigDecimal.valueOf(nights), 2, RoundingMode.HALF_UP);

            totalPerNightRate = totalPerNightRate.add(perNightRate);
            countForNightRate++;
        }

        if (count == 0) {
            avgDiscountPerMonth = BigDecimal.ZERO;   // or return null if you prefer
        }else{
            avgDiscountPerMonth = totalDiscount.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
        }

        forecastingDTO.setAvgDiscountPerMonth(avgDiscountPerMonth);

        if (countForNightRate == 0) {
            avgRoomRatePerMonth = BigDecimal.ZERO;   // or return null if you prefer
        }else{
            avgRoomRatePerMonth = totalPerNightRate.divide(BigDecimal.valueOf(countForNightRate), 2, RoundingMode.HALF_UP);
        }

        forecastingDTO.setAvgDiscountPerMonth(avgDiscountPerMonth);
        forecastingDTO.setAvgRoomRatePerMonth(avgRoomRatePerMonth);

        return forecastingDTO;
    }
}

