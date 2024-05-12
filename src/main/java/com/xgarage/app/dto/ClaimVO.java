package com.xgarage.app.dto;

import java.util.Date;

public interface ClaimVO {
    Long getId();
    Long getBidId();
    String getClaimNo();
    String getClaimTitle();
    String getPartNames();
    Date getClaimDate();
    Long getTenantId();
    String getTenantName();
    String getCreatedUser();
    String getStatus();
    Date getStatusDate();
    Double getTotalPrice();
    Double getLumpSumPrice();
}
