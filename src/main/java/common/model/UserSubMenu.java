package common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSubMenu implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_id")
    private Long role;

    @ManyToOne
    @JoinColumn(name = "sub_menu_id", referencedColumnName = "id")
    private SubMenu subMenu;

    @ManyToOne
    @JoinColumn(name = "user_main_menu_id", referencedColumnName = "id")
//    @JsonIgnore
    private UserMainMenu userMainMenu;

    @Column(nullable = true)
    private boolean newAuth;
    @Column(nullable = true)
    private boolean deleteAuth;
    @Column(nullable = true)
    private boolean editAuth;
    @Column(nullable = true)
    private boolean printAuth;
    @Column(nullable = true)
    private boolean approveAuth;
    @Column(nullable = true)
    private boolean cancelAuth;
    @Column(nullable = true)
    private boolean acceptAuth;
    @Column(nullable = true)
    private boolean completeAuth;
    @Column(nullable = true)
    private boolean viewAuth = false;

    public void update(UserSubMenu updated) {
        this.setSubMenu(updated.getSubMenu());
        this.setAcceptAuth(updated.isAcceptAuth());
        this.setApproveAuth(updated.isApproveAuth());
        this.setRole(updated.getRole());
        this.setCancelAuth(updated.isCancelAuth());
        this.setUserMainMenu(updated.getUserMainMenu());
        this.setCompleteAuth(updated.isCompleteAuth());
        this.setNewAuth(updated.isNewAuth());
        this.setEditAuth(updated.isEditAuth());
        this.setPrintAuth(updated.isPrintAuth());
        this.setDeleteAuth(updated.isDeleteAuth());
        this.setViewAuth(updated.isViewAuth());
    }
}
