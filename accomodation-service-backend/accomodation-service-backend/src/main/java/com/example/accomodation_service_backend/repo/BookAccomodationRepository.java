package com.example.accomodation_service_backend.repo;

import com.example.accomodation_service_backend.model.BookAccomodation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookAccomodationRepository extends JpaRepository<BookAccomodation, String> {

    @Query("""
           SELECT a
           FROM BookAccomodation a
           WHERE LOWER(a.roomSk) = LOWER(:roomSk)
           """)
    List<BookAccomodation> getBookingsUsingRoomSk(@Param("roomSk") String roomSk);
}
