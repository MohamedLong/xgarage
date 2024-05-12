package com.xgarage.app.model;

import lombok.*;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BidLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Id;
    private long actionUserId;
    private String actionUserAccount;
    private Timestamp actionDateTime;
    private long bidId;
    private long requestId;
    private long bidStatusId;
    private long cu;
    @Column(nullable = false, columnDefinition = "decimal default 0.0")
    private double cuRate;
    @Column(nullable = false, columnDefinition = "decimal default 0.0")
    private BigDecimal price;
    @Column(nullable = false, columnDefinition = "decimal default 0.0")
    private BigDecimal servicePrice;
    private int deliverDays;
    private long version;
    private long voiceNoteId;
    private String comments;
    private long reviseVoiceNoteId;
    @Column(nullable = true)
    private String reviseComments;
}
