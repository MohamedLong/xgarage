package com.xgarage.app.dto;

import com.xgarage.app.model.OrderType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BidOrderDto {
    private List<Long> bids;
    private Long customer;
    private Long supplier;
    private Date orderDate;
    private Long paymentMethod;
    private BigDecimal orderAmount;
    private BigDecimal deliveryFees;
    private BigDecimal vat;
    private BigDecimal discount;
    private BigDecimal totalAmount;
    private Long shippingMethod;
    private Long shippingAddress;
    private String phone;
    @Enumerated(EnumType.STRING)
    private OrderType orderType = OrderType.Bid;
}
