package common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMainMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_id")
    private Long role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_root_menu_id", referencedColumnName = "id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UserRootMenu userRootMenu;
//
//    @OneToMany(fetch = FetchType.LAZY,
//            mappedBy = "userMainMenu")
//    @JsonIgnore
//    private List<UserSubMenu> subMenus;

    @ManyToOne
    @JoinColumn(name = "main_menu_id", referencedColumnName = "id")
    private MainMenu mainMenu;

    public void update(UserMainMenu updated) {
        this.setMainMenu(updated.getMainMenu());
        this.setUserRootMenu(updated.getUserRootMenu());
        this.setRole(updated.getRole());
    }


}
