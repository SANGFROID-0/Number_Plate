package com.nirbhay.parking_system.repo;

import com.nirbhay.parking_system.model.ParkingSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingSlotRepo extends JpaRepository <ParkingSlot , Integer >
{

}
