package common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MainMenu implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pageName;

    private String uiComponent;

    private String routerLink;

    private String icon;

    private int pageOrder;

    @ManyToOne
    @JoinColumn(name = "root_menu_id", referencedColumnName = "id")
    @JsonIgnore
    private RootMenu rootMenu;

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "mainMenu")
    @JsonIgnore
    private List<SubMenu> subMenus;

}
