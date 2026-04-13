package com.nirbhay.parking_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDto
{
    private String plateNo;
    private String type;
    private Integer ownerId;
}
