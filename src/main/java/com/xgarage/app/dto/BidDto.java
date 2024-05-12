package com.xgarage.app.dto;
import com.xgarage.app.model.Document;
import com.xgarage.app.model.PartType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BidDto {
    private Long id;
    private Long requestId;
    private Long userid;
    private String userFirstName;
    private Date userCreateDate;
    private Date bidDate;
    private Long statusId;
    private Double price;
    private Double servicePrice;
    private Long cuId;
    private Double cuRate;
    private Long supplierId;
    private Long supplierUserId;
    private String supplierName;
    private int deliverDays;
    private String requestTitle;
    private long submittedBids;
    private long rejectedBids;
    private String comments;
    private Document voiceNote;
    private List<Document> bidImages;
    private String location;
    private int warranty;
    private String reviseComments;
    private Document reviseVoiceNote;
    private String actionComments;
    private PartType partType;
    private List<PartType> requestPartTypes;
}
