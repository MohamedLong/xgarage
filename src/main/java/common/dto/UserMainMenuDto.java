package common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMainMenuDto {

    private Long id;
    private Long pageId;

    private int pageOrder;

    private String pageName;

    private String uiComponent;

    private String routerLink;

    private String icon;

    private List<UserSubMenuDto> subMenus;
}
