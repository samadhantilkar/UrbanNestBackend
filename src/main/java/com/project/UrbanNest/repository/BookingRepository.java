package com.project.UrbanNest.repository;

import com.project.UrbanNest.entity.Booking;
import com.project.UrbanNest.entity.Hotel;
import com.project.UrbanNest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Long> {
    Optional<Booking> findByPaymentSessionId(String sessionId);

    List<Booking> findByHotel(Hotel hotel);

    List<Booking> findByHotelAndCreatedAtBetween(Hotel hotel, LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<Booking> getByUser(User user);
}
