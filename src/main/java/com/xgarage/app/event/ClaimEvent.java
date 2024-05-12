package com.xgarage.app.event;

import com.xgarage.app.model.Status;

public record ClaimEvent(Long requestId, Status status, Long updateBy) {
}
