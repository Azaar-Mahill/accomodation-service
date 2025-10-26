package com.example.accomodation_service_backend.repo;

import com.example.accomodation_service_backend.model.AccommodationLocation;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface AccommodationLocationRepository extends JpaRepository<AccommodationLocation, String> {

    @Query("""
           SELECT a.accommodationLocationSk
           FROM AccommodationLocation a
           """)
    List<String> findAllAccommodationLocationSks();

    @Query("""
           SELECT a.accommodationLocationSk
           FROM AccommodationLocation a
           WHERE LOWER(a.environment) = LOWER(:environment)
           """)
    List<String> findAccommodationLocationSksByEnvironment(@Param("environment") String environment);

    @Query("""
           SELECT a.environment
           FROM AccommodationLocation a
           WHERE LOWER(a.accommodationLocationSk) = LOWER(:accommodationLocationSk)
           """)
    String getEnvironment(@Param("accommodationLocationSk") String accommodationLocationSk);

    @Query("""
           SELECT a.province
           FROM AccommodationLocation a
           WHERE LOWER(a.accommodationLocationSk) = LOWER(:accommodationLocationSk)
           """)
    String getProvince(@Param("accommodationLocationSk") String accommodationLocationSk);

    @Query("""
           SELECT a.district
           FROM AccommodationLocation a
           WHERE LOWER(a.accommodationLocationSk) = LOWER(:accommodationLocationSk)
           """)
    String getDistrict(@Param("accommodationLocationSk") String accommodationLocationSk);

    @Query("""
           SELECT a.city
           FROM AccommodationLocation a
           WHERE LOWER(a.accommodationLocationSk) = LOWER(:accommodationLocationSk)
           """)
    String getCity(@Param("accommodationLocationSk") String accommodationLocationSk);
}


