package com.xgarage.app.dto;

import java.sql.Timestamp;

public interface RequestVO {
    Long getId();
    Long getStatus();
    Timestamp getSubmissionDate();
    Long getUserId();
    Double getQty();
    String getPrivacy();
    String getFirstName();
    String getRequestTitle();
    Long getSubmittedBids();
    Long getRejectedBids();
    Long getSelectedBid();
    Long getJobId();
    Long getClaimId();

    String getJobNo();

    String getClaimNo();
}
