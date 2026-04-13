package com.nirbhay.parking_system.repo;

import com.nirbhay.parking_system.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepo extends JpaRepository <AuditLog, Integer >{
}
