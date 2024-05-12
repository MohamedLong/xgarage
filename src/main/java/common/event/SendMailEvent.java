package common.event;

import ip.sms.smsapp.sms.dto.Attachment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
