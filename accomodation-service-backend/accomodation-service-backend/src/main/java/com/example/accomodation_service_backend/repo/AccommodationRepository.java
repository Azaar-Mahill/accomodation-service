package com.example.accomodation_service_backend.repo;

import com.example.accomodation_service_backend.model.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface AccommodationRepository extends JpaRepository<Accommodation, String> {

    @Query("""
           SELECT a
           FROM Accommodation a
           WHERE LOWER(a.accommodationTypeSk) = LOWER(:accommodationTypeSk)
           """)
    List<Accommodation> getAccomodationsFromTypeSK(@Param("accommodationTypeSk") String accommodationTypeSk);

    @Query("""
           SELECT a
           FROM Accommodation a
           WHERE LOWER(a.accommodationLocationSk) = LOWER(:accommodationLocationSk)
           """)
    List<Accommodation> getAccomodationsFromAccommodationLocationSk(@Param("accommodationLocationSk") String accommodationLocationSk);

    @Query("""
           SELECT a
           FROM Accommodation a
           WHERE LOWER(a.accommodationTypeSk) = LOWER(:accommodationTypeSk)
           """)
    List<Accommodation> getAllAccomodationsFromTypeSK(@Param("accommodationTypeSk") String accommodationTypeSk);


}


