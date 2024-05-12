package common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import common.model.SubMenu;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSubMenuDto {
    private Long pageId;
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private SubMenu subMenu;
    private boolean newAuth;
    private boolean editAuth;
    private boolean deleteAuth;
    private boolean printAuth;
    private boolean approveAuth;
    private boolean cancelAuth;
    private boolean acceptAuth;
    private boolean completeAuth;
}
