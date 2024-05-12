package common.feign;

import common.dto.RequestUserStatsDto;
import common.feign.fallback.CoreFeignFallback;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(value = "xgarage-core-service", fallback = CoreFeignFallback.class)
public interface CoreFeign {

    @RequestMapping(method = RequestMethod.GET, value = "/core/api/v1/request/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    RequestUserStatsDto getRequestUserStatistics(@PathVariable Long userId);

    @RequestMapping(method = RequestMethod.GET, value = "/core/api/v1/supplier/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    Long getSupplierId(@PathVariable Long userId);

    @RequestMapping(method = RequestMethod.GET, value = "/core/api/v1/supplier/{supplierList}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<Long> getUserIdListFromSupplierIdList(@PathVariable List<Long> supplierList);

}
