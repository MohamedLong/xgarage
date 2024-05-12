package com.xgarage.app.dto;

public interface JobsForSupplierVO {
    Long getId();
    String getClient();
    Long getSubmittedBids();
    String getJobTitle();
    Long getStatus();
    String getJobStatus();
    String getTotalPrice();
    String getPartNames();
}
