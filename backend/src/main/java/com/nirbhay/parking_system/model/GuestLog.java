package com.nirbhay.parking_system.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class GuestLog
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer guestLogId;

    private String plateNo;      // raw plate number (not linked to registered vehicle)
    private String action;       // "in" or "out"
    private Date   visitTime;    // timestamp of this event
}
