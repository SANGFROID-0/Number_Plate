package com.nirbhay.parking_system.controller;

import com.nirbhay.parking_system.model.AuditLog;
import com.nirbhay.parking_system.repo.AuditLogRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("audit")
public class AuditLogController
{
    @Autowired
    private AuditLogRepo auditLogRepo;

    // GET /audit/all  →  every audit log entry
    @GetMapping("all")
    public List<AuditLog> getAll()
    {
        return auditLogRepo.findAll();
    }

    // GET /audit/{id}  →  single log by id
    @GetMapping("{id}")
    public AuditLog getById(@PathVariable Integer id)
    {
        return auditLogRepo.findById(id).orElse(null);
    }
}
