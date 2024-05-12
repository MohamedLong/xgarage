package com.xgarage.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class ClaimTickId implements Serializable {

    @Column(name = "claim_id")
    private Long claim;

    @Column(name = "tick_id")
    public Long tick;

    public ClaimTickId(Long claimId, Long tickId) {
        this.claim = claimId;
        this.tick = tickId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClaimTickId claimTicksId = (ClaimTickId) o;
        return Objects.equals(claim, claimTicksId.claim) &&
                Objects.equals(tick, claimTicksId.tick);
    }

    @Override
    public int hashCode() {
        return Objects.hash(claim, tick);
    }
}
