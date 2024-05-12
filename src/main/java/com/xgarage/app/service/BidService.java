package com.xgarage.app.service;

import com.xgarage.app.dto.BidClaimVO;
import com.xgarage.app.dto.BidOrderDto;
import com.xgarage.app.dto.BidVO;
import com.xgarage.app.dto.MultipleBids;
import com.xgarage.app.event.DirectBidEvent;
import com.xgarage.app.model.*;
import com.xgarage.app.utils.RequestStatusConstants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
public interface BidService {
    Bid findProxyBidById(Long id);
    Bid findBidById(Long id);
    BidVO findBidDtoById(Long id);
    List<BidVO> findAllBids(Integer pageNo, Integer pageSize);
    long countBidsBySupplier(Long supplierId);
    long countCompletedDealsBySupplier(Long supplierId);
    Page<Bid> findBidPage(Pageable pageable);
    Bid saveBid(Bid bid, List<MultipartFile> bidImages, MultipartFile voiceNote, Request request);
    boolean deleteBidById(Long id);
    boolean addRequestToBid(Long requestId, Long bidId);
    List<BidVO> findByRequestIdVO(Long requestId, Integer pageNo, Integer pageSize);
    List<Bid> findByRequestId(Long requestId);
    List<BidVO> findBySupplierId(Long supplierId, Integer pageNo, Integer pageSize);
    boolean updateBid(Bid bid, List<MultipartFile> bidImages, MultipartFile voiceNote, Long updatedUser);
    boolean cancelBid(Long bidId, String cancelComments);
    boolean approveBid(Long bidId);
    boolean rejectBid(Long bidId, String rejectComments);
    boolean reviseBid(Long bidId, String reviseComments, MultipartFile reviseVoiceNote);
    boolean completeBid(Long bidId, String completeComments);
    boolean acceptBid(Long bidId);
    default boolean notValidRequest(Request request) {
        return request == null || RequestStatusConstants.COMPLETED_STATUS == request.getStatus().getId() || RequestStatusConstants.CANCELED_STATUS == request.getStatus().getId();
    }
    boolean updateOtherBids(Long bidId, Long requestId, Status newStatus);
    boolean updateAllBids(Long requestId, Status newStatus);
    long countByRequestId(Long requestId);
    long countRejectedByRequestId(Long requestId);
    long countBySupplierId(Long supplierId);
    boolean existsById(Long bidId);
    List<BidVO> findHistoryBySupplierId(Long supplierId, Integer pageNo, Integer pageSize);
    List<Bid> findAllByRequestId(Long id);
    public List<Bid> findBySupplierAndRequest(Long supplierId, Long requestId);

    Long approveBidAndPlaceOrder(BidOrderDto bidOrder, Long customer);

    List<BidVO> findByJobIdVO(Long jobId, Integer pageNo, Integer pageSize);

    boolean approveMultipleBids(BidOrderDto bidOrder, Long customer);

    void createDirectBidForRequest(DirectBidEvent directBidEvent);

    boolean rejectMultipleBids(MultipleBids rejectMultipleBids);

    List<BidVO> findByOrderVO(Long orderId, Integer pageNo, Integer pageSize);

    Long findRequestUserByBid(Long bidId);

    Long findSupplierUserByBid(Long bidId);

    List<BidVO> findByJobIdAndSupplier(Long jobId, Long supplier, Integer pageNo, Integer pageSize);

    boolean cancelMultipleBids(MultipleBids cancelMultipleBids);

    boolean acceptMultipleBids(MultipleBids acceptMultipleBids);

    List<Long> findBidIdListOfOrder(Long orderId);

    boolean completeMultipleBids(MultipleBids completeMultipleBids);

    List<BidClaimVO> findByClaimIdAndSupplier(Long claimId, Long supplier, Integer pageNo, Integer pageSize);

    List<BidClaimVO> findByClaimIdVO(Long claimId, Integer pageNo, Integer pageSize);
}
