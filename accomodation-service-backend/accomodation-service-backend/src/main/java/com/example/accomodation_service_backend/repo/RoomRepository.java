package com.example.accomodation_service_backend.repo;

import com.example.accomodation_service_backend.model.Accommodation;
import com.example.accomodation_service_backend.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, String> {

    @Query("""
           SELECT a.roomSk
           FROM Room a
           WHERE LOWER(a.accommodationSk) = LOWER(:accommodationSK)
           """)
    List<String> getRoomsUsingAccomodationSk(@Param("accommodationSK") String accommodationSK);

    @Query("""
           SELECT a
           FROM Room a
           WHERE LOWER(a.accommodationSk) = LOWER(:accommodationSK)
           """)
    List<Room> getRoomDetailsUsingAccomodationSk(@Param("accommodationSK") String accommodationSK);
}
