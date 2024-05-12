package com.xgarage.app.dto;


import com.xgarage.app.model.Privacy;
import com.xgarage.app.model.Supplier;

import java.util.List;

public record UpdateJobDto(Long id, String jobNumber, Privacy privacy, List<Supplier> supplierList) {
}
