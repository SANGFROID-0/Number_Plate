package com.nirbhay.parking_system.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Vehicle
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer vehicleId;

    private String plateNo;

    @ManyToOne
    private Owner owner;

    @JsonIgnore
    @OneToMany(mappedBy = "vehicle")
    private List<AuditLog> logs;

    private String type;
}
