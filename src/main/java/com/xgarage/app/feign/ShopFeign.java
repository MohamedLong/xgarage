package com.xgarage.app.feign;

import com.xgarage.app.dto.BidOrderDto;
import com.xgarage.app.dto.OrderRequest;
import com.xgarage.app.feign.fallback.ShopFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "xgarage-shop-service", fallback = ShopFeignFallback.class)
public interface ShopFeign {

    @RequestMapping(method = RequestMethod.POST, value = "/store/api/v1/orders/placeOrder", produces = MediaType.APPLICATION_JSON_VALUE)
    Long placeOrder(@RequestBody BidOrderDto bidOrderDto);
    @RequestMapping(method = RequestMethod.POST, value = "/store/api/v1/orders/cancel/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    boolean cancelOrder(@RequestBody OrderRequest orderRequest);
    @RequestMapping(method = RequestMethod.POST, value = "/store/api/v1/orders/seller/cancel/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    boolean cancelOrderBySeller(@RequestBody OrderRequest orderRequest);
    @RequestMapping(method = RequestMethod.POST, value = "/store/api/v1/orders/accept", produces = MediaType.APPLICATION_JSON_VALUE)
    boolean acceptOrder(@RequestBody OrderRequest orderRequest);
    @RequestMapping(method = RequestMethod.POST, value = "/store/api/v1/orders/readyShipping", produces = MediaType.APPLICATION_JSON_VALUE)
    boolean readyForShippingOrder(@RequestBody OrderRequest orderRequest);
    @RequestMapping(method = RequestMethod.GET, value = "/store/api/v1/reviews/calculateSellerAvgRating/{supplierId}", produces = MediaType.APPLICATION_JSON_VALUE)
    double calculateSellerAvgRating(@PathVariable Long supplierId);

}
