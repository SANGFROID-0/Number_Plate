package com.nirbhay.parking_system.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Owner
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ownerId;

    private String name;
    private String flatNo;
    private String phone;

    @JsonIgnore
    @OneToMany(mappedBy = "owner")
    private List<Vehicle> vehicles;

    @JsonIgnore
    @OneToMany(mappedBy = "owner")
    private List<ParkingSlot> slot;
}
