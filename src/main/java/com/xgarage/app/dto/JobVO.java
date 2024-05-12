package com.xgarage.app.dto;

import java.util.Date;

public interface JobVO {
    Long getId();
    String getJobNo();
    String getLocation();
    Date getCreatedAt();
    String getUserId();
    String getJobStatus();
}
