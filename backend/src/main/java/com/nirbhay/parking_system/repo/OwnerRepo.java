package com.nirbhay.parking_system.repo;

import com.nirbhay.parking_system.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OwnerRepo extends JpaRepository <Owner, Integer>{
}
