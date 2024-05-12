package com.xgarage.app.event;

import com.xgarage.app.dto.Attachment;

import java.util.List;

public record NotificationEvent(String principleType, List<Long> principleIds, String type, Long typeId, String title, String message, String topic) {
}
