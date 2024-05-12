package gateway.request;

import lombok.*;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@Setter
@AllArgsConstructor
public class ConnValidationResponse {
    private Long id;
    private Long tenant;
    private Long tenantType;
    private String userId;
    private List<Authorities> authorities;
    private boolean status;
    private boolean isAuthenticated;
}
