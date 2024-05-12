package com.xgarage.app.dto;

import java.util.Date;
import java.util.List;

public interface BidVO {
    Long getBidId();
    String getPartName();
    String getPartType();
    Long getRequestId();
    Long getUserId();
    String getUserFirstName();
    Date getUserCreateDate();
    Date getBidDate();
    Long getStatusId();
    Double getQty();
    Double getPrice();
    Double getOriginalPrice();
    Double getVat();
    Double getDiscount();
    String getDiscountType();
    Double getServicePrice();
    Long getCuId();
    Double getCuRate();
    Long getSupplierId();
    String getSupplierName();
    Integer getDeliverDays();
    String getRequestTitle();
    Long getSubmittedBids();
    Long getRejectedBids();
    String getComments();
    String getLocation();
    Integer getWarranty();
    String getReviseComments();
    String getActionComments();
    String getVoiceNote();
    String getReviseVoiceNote();
    Long getOrderId();
    String getBidImages();
}
