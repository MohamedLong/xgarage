package com.xgarage.app.model;

import lombok.*;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RequestLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private Long actionUserId;
    private String actionUserAccount;
    private Timestamp actionDateTime;
    private Long requestId;
    private Date bidClosingDate;
    private String privacy;
    private Long carId;
    private Long partId;
    private Long requestStatusId;
    private Long version;
}
