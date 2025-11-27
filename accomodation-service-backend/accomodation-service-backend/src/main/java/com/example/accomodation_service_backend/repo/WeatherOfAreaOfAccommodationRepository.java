package com.example.accomodation_service_backend.repo;

import com.example.accomodation_service_backend.model.WeatherOfAreaOfAccommodation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WeatherOfAreaOfAccommodationRepository extends JpaRepository<WeatherOfAreaOfAccommodation, String> {

    @Query("""
           SELECT s
           FROM WeatherOfAreaOfAccommodation s
           WHERE LOWER(s.accommodationLocationSk) = LOWER(:locationSKOfAccomodation)
           AND LOWER(s.monthSk) = LOWER(:month)
           """)
    WeatherOfAreaOfAccommodation getWeatherDetailsOfLocation(
            @Param("locationSKOfAccomodation") String locationSKOfAccomodation,
            @Param("month") String month);

    @Query("""
           SELECT s
           FROM WeatherOfAreaOfAccommodation s
           WHERE LOWER(s.monthSk) = LOWER(:month)
           """)
    List<WeatherOfAreaOfAccommodation> getWeatherDetailsOfLocationBasedOnMonth(
            @Param("month") String month);

    @Query("""
           SELECT s
           FROM WeatherOfAreaOfAccommodation s
           WHERE LOWER(s.accommodationLocationSk) = LOWER(:locationSKOfAccomodation)
           """)
    List<WeatherOfAreaOfAccommodation> getAllWeatherDetailsOfLocation(
            @Param("locationSKOfAccomodation") String locationSKOfAccomodation);
}
