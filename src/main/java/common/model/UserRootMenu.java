package common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRootMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_id")
    private Long role;

//    @OneToMany(fetch = FetchType.LAZY,
//            mappedBy = "userRootMenu")
//    @JsonIgnore
//    private List<UserMainMenu> userMainMenus;

    @ManyToOne
    @JoinColumn(name = "root_menu_id", referencedColumnName = "id")
    private RootMenu rootMenu;
}
