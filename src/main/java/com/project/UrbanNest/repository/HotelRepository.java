package com.project.UrbanNest.repository;

import com.project.UrbanNest.entity.Hotel;
import com.project.UrbanNest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel , Long> {
    List<Hotel> findByOwner(User owner);
}
