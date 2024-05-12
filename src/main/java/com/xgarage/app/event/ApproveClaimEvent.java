package com.xgarage.app.event;

import com.xgarage.app.model.Status;

public record ApproveClaimEvent(Long requestId, Status status, Long updateBy, Long tenantId) {
}
