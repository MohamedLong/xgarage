package com.xgarage.app.dto;

import com.xgarage.app.model.Claim;

import java.util.List;

public record UpdateClaimDto(Claim claim, List<ClaimPartsDto> claimPartsDtoList) {
}
