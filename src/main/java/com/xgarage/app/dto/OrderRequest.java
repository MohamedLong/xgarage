package com.xgarage.app.dto;

public record OrderRequest(Long orderId, Long sellerId, boolean multipleBid) {
}
