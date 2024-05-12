package common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String token;
    private boolean enabled;
    private Date createdDate;
    private List<String> roles;
    private Long tenantId;
    private String tenant;
    private long submittedRequests = 0;
    private long completedDeals = 0;
    private String userImage;
    private double rating;
}
