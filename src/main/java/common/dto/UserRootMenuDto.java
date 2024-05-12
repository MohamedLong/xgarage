package common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRootMenuDto {

    private Long moduleId;

    private String moduleName;

    private String icon;

    private int pageOrder;

}
