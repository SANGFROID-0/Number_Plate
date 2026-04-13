package com.nirbhay.parking_system.service;

import com.nirbhay.parking_system.dto.RegisterRequestDto;
import com.nirbhay.parking_system.model.AuditLog;
import com.nirbhay.parking_system.model.GuestLog;
import com.nirbhay.parking_system.model.Owner;
import com.nirbhay.parking_system.model.ParkingSlot;
import com.nirbhay.parking_system.model.Vehicle;
import com.nirbhay.parking_system.repo.AuditLogRepo;
import com.nirbhay.parking_system.repo.GuestLogRepo;
import com.nirbhay.parking_system.repo.OwnerRepo;
import com.nirbhay.parking_system.repo.ParkingSlotRepo;
import com.nirbhay.parking_system.repo.VehicleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class ParkingService
{
    @Autowired
    private AuditLogRepo auditLogRepo;
    @Autowired
    private OwnerRepo ownerRepo;
    @Autowired
    private ParkingSlotRepo parkingSlotRepo;
    @Autowired
    private VehicleRepo vehicleRepo;

    @Autowired
    private GuestLogRepo guestLogRepo;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd MMM yyyy, hh:mm a");


    public Vehicle searchVehicle(String plateNo)
    {
        return vehicleRepo.findByPlateNo(plateNo).orElse(null);
    }

    public String processIncomingVehicle(Vehicle vehicle)
    {
        Owner owner = vehicle.getOwner();

        if(owner == null)
        {
            return "Owner not found";
        }

        List<ParkingSlot> slots = owner.getSlot();

        if(slots == null || slots.isEmpty())
        {
            return "No slot allocated";
        }

        ParkingSlot allottedSlot = null;

        for(ParkingSlot slot : slots)
        {
            if(!slot.isOccupied())
            {
                allottedSlot = slot;
                break;
            }
        }

        if(allottedSlot == null)
        {
            return "All Slots Occupied";
        }

        allottedSlot.setOccupied(true);
        parkingSlotRepo.save(allottedSlot);

        AuditLog log = new AuditLog();
        log.setVehicle(vehicle);
        log.setAction("in");
        log.setInTime(new Date());
        log.setOutTime(null);

        auditLogRepo.save(log);

        return "audit logged";
    }

    public void registerVehicle(RegisterRequestDto request)
    {
        Owner owner = ownerRepo.findById(request.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        Vehicle vehicle = new Vehicle();

        vehicle.setPlateNo(request.getPlateNo());
        vehicle.setType((request.getType()));
        vehicle.setOwner(owner);

        vehicleRepo.save(vehicle);

    }

    public Owner addOwner(Owner owner)
    {
        return ownerRepo.save(owner);
    }

    public String allocateSlot(int ownerId)
    {
        Owner owner = ownerRepo.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        ParkingSlot slot = new ParkingSlot();
        slot.setOwner(owner);
        slot.setOccupied(false);

        parkingSlotRepo.save(slot);

        return "slot allocated";
    }

    public String processOutgoingVehicle(Vehicle vehicle)
    {
        Owner owner = vehicle.getOwner();

        if(owner == null)
        {
            return "Owner not found";
        }

        List<ParkingSlot> slots = owner.getSlot();

        if(slots == null || slots.isEmpty())
        {
            return "No slot allocated";
        }

        ParkingSlot deAllocatedSlot = null;

        for(ParkingSlot slot : slots)
        {
            if(slot.isOccupied())
            {
                deAllocatedSlot = slot;
                break;
            }
        }

        if(deAllocatedSlot == null)
        {
            return "Wrong Camera Detection";
        }

        deAllocatedSlot.setOccupied(false);
        parkingSlotRepo.save(deAllocatedSlot);

        AuditLog log = new AuditLog();
        log.setVehicle(vehicle);
        log.setAction("out");
        log.setInTime(null);
        log.setOutTime(new Date());

        auditLogRepo.save(log);

        return "audit logged";
    }
    public List<Owner> getAllOwners()
    {
        return ownerRepo.findAll();
    }

    public List<Vehicle> getAllVehicles()
    {
        return vehicleRepo.findAll();
    }

    public List<ParkingSlot> getAllSlots()
    {
        return parkingSlotRepo.findAll();
    }

    public List<AuditLog> getAllLogs()
    {
        return auditLogRepo.findAll();
    }

    // ── Guest vehicle logic ────────────────────────────────────────────────

    public String logGuestEntry(String plateNo)
    {
        // Check visit history before logging new entry
        List<GuestLog> history = guestLogRepo.findByPlateNoOrderByVisitTimeDesc(plateNo);

        String message;
        if (history.isEmpty())
        {
            message = "First-time visitor — plate: " + plateNo;
        }
        else
        {
            Date lastVisit = history.get(0).getVisitTime();
            message = "Returning visitor. Last seen: " + SDF.format(lastVisit) + " [" + plateNo + "]";
        }

        GuestLog log = new GuestLog();
        log.setPlateNo(plateNo);
        log.setAction("in");
        log.setVisitTime(new Date());
        guestLogRepo.save(log);

        System.out.println("[Guest] " + message);
        return message;
    }

    public String logGuestExit(String plateNo)
    {
        GuestLog log = new GuestLog();
        log.setPlateNo(plateNo);
        log.setAction("out");
        log.setVisitTime(new Date());
        guestLogRepo.save(log);
        return "Guest vehicle exited: " + plateNo;
    }

    public List<GuestLog> getAllGuestLogs()
    {
        return guestLogRepo.findAll();
    }

    public List<GuestLog> getGuestHistory(String plateNo)
    {
        return guestLogRepo.findByPlateNoOrderByVisitTimeDesc(plateNo);
    }
}
