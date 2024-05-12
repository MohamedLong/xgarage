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
public class RootMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String moduleName;

    private String icon;

    private int pageOrder;

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "rootMenu")
    @JsonIgnore
    private List<MainMenu> mainMenus;

}
