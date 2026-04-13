package com.nirbhay.parking_system.controller;

import com.nirbhay.parking_system.dto.RegisterRequestDto;
import com.nirbhay.parking_system.model.AuditLog;
import com.nirbhay.parking_system.model.GuestLog;
import com.nirbhay.parking_system.model.Owner;
import com.nirbhay.parking_system.model.ParkingSlot;
import com.nirbhay.parking_system.model.Vehicle;
import com.nirbhay.parking_system.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class ParkingController
{
    @Autowired
    private ParkingService service;

    // ── POST endpoints (existing) ────────────────────────────────────────────

    @PostMapping("register")
    public String registerVehicle(@RequestBody RegisterRequestDto request)
    {
        service.registerVehicle(request);
        return "Successfully registered";
    }

    @PostMapping("inCameraDetected/{plateNo}")
    public String processIncomingVehicle(@PathVariable("plateNo") String plateNo)
    {
        Vehicle vehicle = service.searchVehicle(plateNo);
        if(vehicle == null)
        {
            // Unknown plate — log as guest and return visit history message
            return service.logGuestEntry(plateNo);
        }
        return service.processIncomingVehicle(vehicle);
    }

    @PostMapping("outCameraDetected/{plateNo}")
    public String processOutgoingVehicle(@PathVariable("plateNo") String plateNo)
    {
        Vehicle vehicle = service.searchVehicle(plateNo);
        if(vehicle == null)
        {
            // Unknown plate on exit — log guest exit
            return service.logGuestExit(plateNo);
        }
        return service.processOutgoingVehicle(vehicle);
    }

    @PostMapping("allocateSlot")
    public String allocateSlots(@RequestBody Owner owner)
    {
        return service.allocateSlot(owner.getOwnerId());
    }

    @PostMapping("addOwner")
    public Owner addOwner(@RequestBody Owner owner)
    {
        return service.addOwner(owner);
    }

    // ── GET endpoints (for dashboard) ───────────────────────────────────────

    @GetMapping("owners")
    public List<Owner> getAllOwners()
    {
        return service.getAllOwners();
    }

    @GetMapping("vehicles")
    public List<Vehicle> getAllVehicles()
    {
        return service.getAllVehicles();
    }

    @GetMapping("slots")
    public List<ParkingSlot> getAllSlots()
    {
        return service.getAllSlots();
    }

    @GetMapping("logs")
    public List<AuditLog> getAllLogs()
    {
        return service.getAllLogs();
    }

    @GetMapping("guestLogs")
    public List<GuestLog> getAllGuestLogs()
    {
        return service.getAllGuestLogs();
    }

    @GetMapping("guestHistory/{plateNo}")
    public List<GuestLog> getGuestHistory(@PathVariable String plateNo)
    {
        return service.getGuestHistory(plateNo);
    }
}
