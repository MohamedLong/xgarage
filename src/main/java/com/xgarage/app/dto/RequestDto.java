package com.xgarage.app.dto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class RequestDto {
    private Long id;
    private Long status;
    private Timestamp submissionDate;
    private Long userId;
    private String firstName;
    private String requestTitle;
    private long submittedBids ;
    private long rejectedBids;
    private BidDto selectedBid;

    private Long jobId;

    public RequestDto(Long id, Long status, Timestamp submissionDate, Long userId, String firstName, String requestTitle, long submittedBids, long rejectedBids, BidDto selectedBid, Long claimId) {
        this.id = id;
        this.status = status;
        this.submissionDate = submissionDate;
        this.userId = userId;
        this.firstName = firstName;
        this.requestTitle = requestTitle;
        this.submittedBids = submittedBids;
        this.rejectedBids = rejectedBids;
        this.selectedBid = selectedBid;
        this.jobId = claimId;
    }
}
