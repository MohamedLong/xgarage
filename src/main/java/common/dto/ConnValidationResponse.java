package common.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Getter
@Builder
@ToString
public class ConnValidationResponse {
    private Long id;
    private Long tenant;
    private Long tenantType;
    private String userId;
    private List<GrantedAuthority> authorities;
    private boolean status;
    private boolean isAuthenticated;
}
