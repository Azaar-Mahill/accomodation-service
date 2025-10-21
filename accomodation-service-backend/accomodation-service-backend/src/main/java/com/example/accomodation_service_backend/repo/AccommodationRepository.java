package com.example.accomodation_service_backend.repo;

import com.example.accomodation_service_backend.model.Accommodation;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccommodationRepository extends JpaRepository<Accommodation, String> {

    @Query("""
           SELECT a FROM Accommodation a
           WHERE (:environment IS NULL OR LOWER(a.environment) = LOWER(:environment))
             AND (:type IS NULL OR LOWER(a.type) = LOWER(:type))
           """)
    List<Accommodation> search(@Param("environment") String environment,
                               @Param("type") String type);
}

