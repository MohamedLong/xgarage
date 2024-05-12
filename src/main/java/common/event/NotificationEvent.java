package common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEvent {
    private Long tenantType;
    private String principleType;
    private List<Long> principleIds;
    private String type;
    private Long typeId;
    private String title;
    private String message;
    private String topic;
}
