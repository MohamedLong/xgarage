package common.feign.fallback;

import common.dto.RequestUserStatsDto;
import common.feign.CoreFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class CoreFeignFallback implements CoreFeign {
    @Override
    public RequestUserStatsDto getRequestUserStatistics(Long userId) {
        log.info("inside CoreFeignFallback: " + userId);
        return new RequestUserStatsDto(0L, 0L);
    }

    @Override
    public Long getSupplierId(Long userId) {
        return null;
    }

    @Override
    public List<Long> getUserIdListFromSupplierIdList(List<Long> supplierList) {
        return null;
    }
}