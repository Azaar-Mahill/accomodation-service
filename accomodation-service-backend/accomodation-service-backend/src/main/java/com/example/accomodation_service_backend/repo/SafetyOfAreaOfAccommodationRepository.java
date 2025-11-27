package com.example.accomodation_service_backend.repo;

import com.example.accomodation_service_backend.model.SafetyOfAreaOfAccommodation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SafetyOfAreaOfAccommodationRepository extends JpaRepository<SafetyOfAreaOfAccommodation, String> {

    // ✅ Fetch all safety records
    @Query("""
           SELECT s
           FROM SafetyOfAreaOfAccommodation s
           """)
    List<SafetyOfAreaOfAccommodation> findAllSafetyFactsOfAccommodations();

    // ✅ Fetch safety records filtered by month
    @Query("""
           SELECT s
           FROM SafetyOfAreaOfAccommodation s
           WHERE LOWER(s.monthSk) = LOWER(:month)
           """)
    List<SafetyOfAreaOfAccommodation> findAllSafetyFactsOfAccommodationsByMonth(@Param("month") String month);

    @Query("""
           SELECT s
           FROM SafetyOfAreaOfAccommodation s
           WHERE LOWER(s.accommodationLocationSk) = LOWER(:locationSKOfAccomodation)
           AND LOWER(s.monthSk) = LOWER(:month)
           """)
    SafetyOfAreaOfAccommodation getSafetyDetailsOfLocation(
            @Param("locationSKOfAccomodation") String locationSKOfAccomodation,
            @Param("month") String month);

    @Query("""
           SELECT s
           FROM SafetyOfAreaOfAccommodation s
           WHERE LOWER(s.accommodationLocationSk) = LOWER(:locationSKOfAccomodation)

           """)
    List<SafetyOfAreaOfAccommodation> getSafetyDetailsOfLocationUsingLocationSKOfAccomodation(
            @Param("locationSKOfAccomodation") String locationSKOfAccomodation);
}