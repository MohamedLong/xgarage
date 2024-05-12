package com.xgarage.app.dto;

import com.xgarage.app.model.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierDto {
    private Long Id;
    private Long userId;
    private String name;
    private String email;
    private String cr;
    private String contactName;
    private String phone;
    Set<Location> locations;
    private boolean enabled;
    private long submittedBids = 0;
    private long completedDeals = 0;
    private double rating;
}
