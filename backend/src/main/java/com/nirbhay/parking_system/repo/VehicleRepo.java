package com.nirbhay.parking_system.repo;

import com.nirbhay.parking_system.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleRepo extends JpaRepository <Vehicle , Integer>
{

    Optional<Vehicle> findByPlateNo(String plateNo);
}
