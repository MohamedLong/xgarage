package com.xgarage.app.dto;

import com.xgarage.app.model.PartOption;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public record ClaimPartsDto(Long partId, @Enumerated(EnumType.STRING) PartOption partOption) {
}
