package com.xgarage.app.event;

import java.util.Date;

public record DirectBidEvent(Long requestId, Date createdDate, Long garageId) {
}
