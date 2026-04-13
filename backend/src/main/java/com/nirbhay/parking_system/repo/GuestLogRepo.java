package com.nirbhay.parking_system.repo;

import com.nirbhay.parking_system.model.GuestLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuestLogRepo extends JpaRepository<GuestLog, Integer>
{
    // All visits for a specific plate, ordered newest first
    List<GuestLog> findByPlateNoOrderByVisitTimeDesc(String plateNo);

    // Check if plate has any previous visits
    boolean existsByPlateNo(String plateNo);
}
