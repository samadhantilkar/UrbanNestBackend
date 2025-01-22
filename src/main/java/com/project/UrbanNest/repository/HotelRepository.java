package com.project.UrbanNest.repository;

import com.project.UrbanNest.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<Hotel , Long> {
}
