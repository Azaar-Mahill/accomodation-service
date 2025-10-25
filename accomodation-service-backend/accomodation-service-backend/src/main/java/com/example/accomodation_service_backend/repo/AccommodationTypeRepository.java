package com.example.accomodation_service_backend.repo;


import com.example.accomodation_service_backend.model.AccommodationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccommodationTypeRepository extends JpaRepository<AccommodationType, String> {

    @Query("""
           SELECT a.accommodationTypeSk
           FROM AccommodationType a
           """)
    List<String> findAllAccommodationTypeSks();

    @Query("""
           SELECT a.accommodationTypeSk
           FROM AccommodationType a
           WHERE LOWER(a.accommodationTypeName) = LOWER(:name)
           """)
    List<String> findAccommodationTypeSksByTypeName(@Param("name") String name);
}

