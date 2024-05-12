package com.xgarage.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "claim_selected_tick")
public class ClaimSelectedTick{

    @EmbeddedId
    @JsonIgnore
    public ClaimTickId primaryKey = new ClaimTickId();

    @ManyToOne
    @MapsId("claimId")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Claim claim;

    @ManyToOne
    @MapsId("tickId")
    private ClaimTicks tick;

    private String remarks;

    public ClaimSelectedTick(Claim claim, ClaimTicks tick, String remarks) {
        this.claim = claim;
        this.tick = tick;
        this.remarks = remarks;
        this.primaryKey = new ClaimTickId(claim.getId(), tick.getId());
    }

    @Override
    public String toString() {
        return "Claim ID: " + getClaim().getId() + ", Tick ID: " + getTick().getId() + ", Remarks: " + getRemarks();
    }
}
