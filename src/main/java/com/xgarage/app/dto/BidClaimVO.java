package com.xgarage.app.dto;

import java.util.Date;

public interface BidClaimVO {
    Long getBidId();
    Long getRequestId();
    Long getUserId();
    String getUserFirstName();
    Date getBidDate();
    Long getStatusId();
    String getStatusName();
    Double getPrice();
    Double getOriginalPrice();
    Double getVat();
    Double getDiscount();
    Double getServicePrice();
    Long getSupplierId();
    String getSupplierName();
    Integer getDeliverDays();
    Integer getWarranty();
    Long getOrderId();
    Double getLumpSumPrice();
}
