package com.xgarage.app.feign.fallback;

import com.xgarage.app.dto.BidOrderDto;
import com.xgarage.app.dto.OrderRequest;
import com.xgarage.app.feign.ShopFeign;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

@Component
public class ShopFeignFallback implements ShopFeign {
    @Override
    public Long placeOrder(BidOrderDto bidOrderDto) {
        return null;
    }

    @Override
    public boolean cancelOrder(OrderRequest orderRequest) {
        return false;
    }

    @Override
    public boolean cancelOrderBySeller(OrderRequest orderRequest) {
        return false;
    }

    @Override
    public boolean acceptOrder(OrderRequest orderRequest) {
        return false;
    }

    @Override
    public boolean readyForShippingOrder(OrderRequest orderRequest) {
        return false;
    }

    @Override
    public double calculateSellerAvgRating(Long supplierId) {
        return 0;
    }
}
