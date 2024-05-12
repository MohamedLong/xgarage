package com.xgarage.app.dto;

import java.util.List;

public record ClaimDto(Long claimId, String claimNo, List<JobVO> jobs) {
}
