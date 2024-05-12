package com.xgarage.app.event;

import com.xgarage.app.dto.Attachment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendMailEvent {
    private String title;
    private String body;
    private String from;
    private String to;
    private String subject;
    private String service;
    private Long serviceId;
    private Attachment attachment;
}
