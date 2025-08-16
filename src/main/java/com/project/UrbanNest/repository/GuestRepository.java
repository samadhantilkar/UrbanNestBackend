package com.project.UrbanNest.repository;

import com.project.UrbanNest.entity.Guest;
import com.project.UrbanNest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuestRepository extends JpaRepository<Guest,Long> {
    List<Guest> findByUser(User user);
}
