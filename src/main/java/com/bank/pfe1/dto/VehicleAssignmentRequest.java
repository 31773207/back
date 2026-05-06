package com.bank.pfe1.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class VehicleAssignmentRequest {
    private LocalDate startDate;
    private LocalDate endDate;
}
