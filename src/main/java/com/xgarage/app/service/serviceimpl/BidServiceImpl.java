package com.xgarage.app.service.serviceimpl;

import com.xgarage.app.event.ApproveClaimEvent;
import com.xgarage.app.event.ClaimEvent;
import com.xgarage.app.event.DirectBidEvent;
import com.xgarage.app.feign.KernelFeign;
import com.xgarage.app.feign.ShopFeign;
import com.xgarage.app.dto.*;
import com.xgarage.app.service.*;
import com.xgarage.app.model.*;
import com.xgarage.app.repository.BidRepository;
import com.xgarage.app.utils.OperationCode;
import com.xgarage.app.utils.RequestStatusConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BidServiceImpl implements BidService{

    @Autowired private BidRepository bidRepository;
    @Autowired private StatusService statusService;
    @Autowired private RequestService requestService;
    @Autowired private CurrencyService currencyService;
    @Autowired private DocumentService documentService;
    @Autowired private ShopFeign shopFeign;
    @Autowired private ApplicationEventPublisher publisher;
    @Autowired private GeneralBusinessRulesService businessRulesService;

    @Autowired private KernelFeign kernelFeign;

    private Status openStatus;
    private Status initialApprovalStatus;
    private Status onHoldStatus;
    private Status completedStatus;
    private Status rejectedStatus;
    private Status approvedStatus;
    private Status canceledStatus;
    private Status revisionStatus;
    private Status lostStatus;
    private Status revisedStatus;
    private Status confirmedStatus;


    @Override
    @Transactional(readOnly = true)
    public Bid findProxyBidById(Long id){return bidRepository.getReferenceById(id);}

    @Override
    @Transactional(readOnly = true)
    public Bid findBidById(Long id){
        Optional<Bid> bidOptional = bidRepository.findById(id);
        return bidOptional.orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public BidVO findBidDtoById(Long id){
        return bidRepository.findBidById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BidVO> findAllBids(Integer pageNo, Integer pageSize){
        Pageable page = PageRequest.of(pageNo, pageSize);
        return bidRepository.findAllBids(page);
    }

    @Override
    public long countBidsBySupplier(Long supplierId) {
        return bidRepository.countBidsBySupplierId(supplierId);
    }

    @Override
    public long countCompletedDealsBySupplier(Long supplierId) {
        return bidRepository.countCompletedDealsBySupplierId(supplierId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Bid> findBidPage(Pageable pageable){return bidRepository.findAll(pageable);}

    @Override
    @Transactional(rollbackFor = {Exception.class, IOException.class})
    public Bid saveBid(Bid bid, List<MultipartFile> bidImages, MultipartFile voiceNote, Request request) {
        Bid savedBid = new Bid();
        try{
            log.info("Show the status of transaction in saveBid: " + TransactionSynchronizationManager.isActualTransactionActive());
            Integer noOfSubmittedBids = bidRepository.countBySupplierAndRequest(bid.getSupplier(), request.getId());
            if(validateNotExceedsNoOfAllowedBids(bid.getSupplier(), noOfSubmittedBids)) {
                savedBid.setOperationCode(OperationCode.OUT_OF_ALLOWED_BIDS_CODE);
                return savedBid;
            }
            if(RequestStatusConstants.CANCELED_STATUS == request.getStatus().getId()) {
                savedBid.setOperationCode(OperationCode.Cancelled_Request);
                return savedBid;
            }
            if(RequestStatusConstants.COMPLETED_STATUS == request.getStatus().getId()) {
                savedBid.setOperationCode(OperationCode.Completed_Request);
                return savedBid;
            }
            log.info("bidImages(before): " + bidImages);
            if(bidImages != null) {
                log.info("bidImages(inside): " + bidImages);
                bid.setImages(bidImages.stream().map(documentService::saveDocument).collect(Collectors.toList()));
            }
            if(voiceNote != null) {
                bid.setVoiceNote(documentService.saveDocument(voiceNote));
            }
            if(bid.getQty()== 0.0) {
                bid.setQty(1.0);
            }
//            bid.setSupplier(supplier);
            bid.setRequest(request);
            Date bidDate = new Date();
            bid.setBidDate(bidDate);
            if(RequestStatusConstants.INITIAL_APPROVE == request.getStatus().getId() || RequestStatusConstants.APPROVED_STATUS == request.getStatus().getId()) {
                bid.setStatus(statusService.getStatus(RequestStatusConstants.ONHOLD_STATUS));
            }else{
                bid.setStatus(statusService.getStatus(RequestStatusConstants.OPEN_STATUS));
            }
            Currency currency;
            if(bid.getCu() != null) {
                currency = currencyService.findById(bid.getCu().getId());
            }else{
                currency = currencyService.findById(1L);
            }
            bid.setCu(currency);
            savedBid = bidRepository.save(bid);
            savedBid.setOperationCode(OperationCode.SUCCESS_CODE);
            return savedBid;
        }catch(Exception e) {
            e.printStackTrace();
            log.info("Error inside saveBid: " + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            savedBid.setOperationCode(OperationCode.GENERAL_ERROR_CODE);
            return savedBid;
        }
    }

    @Override
    public boolean deleteBidById(Long id){
        try {
            bidRepository.deleteById(id);
            return true;
        } catch (Exception ex){
            ex.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    @Override
    public boolean addRequestToBid(Long requestId, Long bidId){
        try {
            Request request = requestService.findProxyRequestById(requestId);
            Bid bid = findProxyBidById(bidId);
            bid.setRequest(request);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    @Override
    @Transactional
    @EventListener
    public void createDirectBidForRequest(DirectBidEvent directBidEvent) {
        Bid bid = new Bid();
        approvedStatus = statusService.getStatus(RequestStatusConstants.APPROVED_STATUS);
        Request request = requestService.findRequestById(directBidEvent.requestId());
        bid.setRequest(request);
        bid.setSupplier(directBidEvent.garageId());
        bid.setBidDate(directBidEvent.createdDate());
        bid.setStatus(approvedStatus);
        bid.setPrice(0.0);
        bid.setServicePrice(0.0);
        bid.setQty(1.0);
        bid.setDiscount(0.0);
        bid.setOriginalPrice(0.0);
        bid.setVat(0.0);
        Long tenantAdminUserId = kernelFeign.getTenantAdmin(directBidEvent.garageId());
        bid.setCreateUser(tenantAdminUserId);
        Bid savedBid = bidRepository.save(bid);
        if(savedBid != null) {
            request.setStatus(approvedStatus);
            publisher.publishEvent(new ApproveClaimEvent(directBidEvent.requestId(), approvedStatus, tenantAdminUserId, directBidEvent.garageId()));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BidVO> findByRequestIdVO(Long requestId, Integer pageNo, Integer pageSize) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try{
            Pageable page = PageRequest.of(pageNo, pageSize);
            List<BidVO> bids = bidRepository.findByRequestIdVO(requestId, page);
            stopWatch.stop();
            log.info("Time Elapsed in findByRequestId: " + stopWatch.getTotalTimeSeconds());
            return bids;
        }catch(Exception e) {
            log.info("bids returned exception: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Bid> findByRequestId(Long requestId) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try{
            List<Bid> bids = bidRepository.findByRequestId(requestId);
            stopWatch.stop();
            log.info("Time Elapsed in findByRequestId: " + stopWatch.getTotalTimeSeconds());
            return bids;
        }catch(Exception e) {
            log.info("bids returned exception: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BidVO> findBySupplierId(Long supplierId, Integer pageNo, Integer pageSize) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try{
            Pageable page = PageRequest.of(pageNo, pageSize);
            List<BidVO> bids = bidRepository.findBySupplierId(supplierId, page);
            stopWatch.stop();
            log.info("Time Elapsed in findBySupplierId: " + stopWatch.getTotalTimeSeconds());
            return bids;
        }catch(Exception e) {
            log.info("bids returned exception: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, IOException.class})
    public boolean updateBid(Bid bid, List<MultipartFile> bidImages, MultipartFile voiceNote, Long updatedUser) {
        log.info("Show the status of transaction in UpdateBid: " + TransactionSynchronizationManager.isActualTransactionActive());
        revisedStatus = statusService.getStatus(RequestStatusConstants.REVISED_STATUS);
        try{
            Bid dbBid = bidRepository.getReferenceById(bid.getId());
            if(dbBid == null) {
                log.info("Inside updateBid When dbBid is null");
                return false;
            }
//            Supplier supplier = supplierService.findSupplierById(dbBid.getSupplier());
//            Request request = requestService.findRequestByIdWithoutStatistics(dbBid.getRequest().getId());
//            if(supplier != null && request != null) {
                List<Bid> found = bidRepository.findBySupplierAndRequest(dbBid.getSupplier(), bid.getRequest().getId());
                if(found.size() == 0) {
                    log.info("Inside updateBid When found.size() = 0");
                    return false;
                }
//            }
            if(bidImages != null) {
                dbBid.setImages(bidImages.stream().map(documentService::saveDocument).collect(Collectors.toList()));
            }
            if(voiceNote != null) {
                dbBid.setVoiceNote(documentService.saveDocument(voiceNote));
            }
            if(bid.getServicePrice() != 0.0)
                dbBid.setServicePrice(bid.getServicePrice());
            if(bid.getCuRate() != 0.0)
                dbBid.setCuRate(bid.getCuRate());
            if(bid.getBidDate() != null)
                dbBid.setBidDate(bid.getBidDate());
            if(bid.getPrice() != 0.0)
                dbBid.setPrice(bid.getPrice());
            if(bid.getCu() != null)
                dbBid.setCu(bid.getCu());
            if(bid.getComments() != null)
                dbBid.setComments(bid.getComments());
            if(bid.getDeliverDays() != 0)
                dbBid.setDeliverDays(bid.getDeliverDays());
            if(bid.getLocation() != null)
                dbBid.setLocation(bid.getLocation());
            if(bid.getWarranty() != 0)
                dbBid.setWarranty(bid.getWarranty());
            dbBid.setStatus(revisedStatus);
            Bid savedBid = bidRepository.save(dbBid);
            if(bid != null) {
                savedBid.setUpdateUser(updatedUser);
                return true;
            }
        }catch(Exception e) {
            e.printStackTrace();
            log.info("Error Inside updateBid: " + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        log.info("Inside updateBid When with no error");
        return false;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, IOException.class})
    public boolean cancelBid(Long bidId, String cancelComments) {
        log.info("Show the status of transaction in cancelBid: " + TransactionSynchronizationManager.isActualTransactionActive());
        openStatus = statusService.getStatus(RequestStatusConstants.OPEN_STATUS);
        canceledStatus = statusService.getStatus(RequestStatusConstants.CANCELED_STATUS);
        confirmedStatus = statusService.getStatus(RequestStatusConstants.CONFIRMED_STATUS);
        try{
            Bid bid = bidRepository.getReferenceById(bidId);
            if(applyCancelBidBusinessRule(bid, cancelComments)) {
                if(RequestKind.Service.equals(bid.getRequest().getRequestType())) {
                    publisher.publishEvent(new ClaimEvent(bid.getRequest().getId(), confirmedStatus, bid.getUpdateUser()));
                }
                if(handleCancelOrder(bid)) {
                    return true;
                }
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return false;
            }
            return false;
        }catch(Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    @Override
    @Transactional
    public boolean cancelMultipleBids(MultipleBids cancelMultipleBids) {
        log.info("Show the status of transaction in cancelMultipleBid: " + TransactionSynchronizationManager.isActualTransactionActive());
        openStatus = statusService.getStatus(RequestStatusConstants.OPEN_STATUS);
        canceledStatus = statusService.getStatus(RequestStatusConstants.CANCELED_STATUS);
        try {
            List<Bid> bids = bidRepository.findAllBidsOfIdIn(cancelMultipleBids.bids());
            AtomicBoolean failedTransaction = new AtomicBoolean(false);
            bids.stream()
                    .map(bid -> applyCancelBidBusinessRule(bid, ""))
                    .toList()
                    .stream()
                    .filter(r -> !r)
                    .findAny()
                    .ifPresent(b -> {
                        failedTransaction.set(true);
                    });
            if(failedTransaction.get()) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return false;
            }
            return true;
        }catch(Exception e) {
            log.info("Inside BidService.cancelMultipleBids: " + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    public boolean handleCancelOrder(Bid bid) {
        Long orderId = bid.getOrder();
        if(orderId != null) {
            if(shopFeign.cancelOrderBySeller(new OrderRequest(orderId, null, false))) {
                return true;
            }
            return false;
        }
        return true;
    }

    public boolean applyCancelBidBusinessRule(Bid bid, String cancelComments) {
        Request request = bid.getRequest();
        if (notValidRequest(request)) {
            return false;
        }
        if (RequestStatusConstants.COMPLETED_STATUS != bid.getStatus().getId() && RequestStatusConstants.CANCELED_STATUS != bid.getStatus().getId() && RequestStatusConstants.REJECTED_STATUS != bid.getStatus().getId()) {
            boolean updateOtherBidsStatus = updateOtherBids(bid.getId(), request.getId(), openStatus);
            if (RequestStatusConstants.INITIAL_APPROVE == bid.getStatus().getId() || RequestStatusConstants.APPROVED_STATUS == bid.getStatus().getId() && updateOtherBidsStatus) {
                request.setStatus(openStatus);
                request.setUpdateUserId(0L);
            }
            bid.setStatus(canceledStatus);
            bid.setUpdateUser(bid.getSupplier());
            bid.setActionComments(cancelComments);
            return true;
        }
        return false;
    }


    @Override
    @Transactional(rollbackFor = {Exception.class, IOException.class})
    public boolean approveBid(Long bidId) {
        log.info("Show the status of transaction in approveBid: " + TransactionSynchronizationManager.isActualTransactionActive());
        initialApprovalStatus = statusService.getStatus(RequestStatusConstants.INITIAL_APPROVE);
        onHoldStatus = statusService.getStatus(RequestStatusConstants.ONHOLD_STATUS);
        try{
            Bid bid = bidRepository.getReferenceById(bidId);
            return applyApproveBidBusinessRule(bid);
        }catch(Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.info("Error inside BidService: " + e.getMessage());
            return false;
        }
    }

    public boolean applyApproveBidBusinessRule(Bid bid) {
        Request request = bid.getRequest();
        if(RequestStatusConstants.OPEN_STATUS != request.getStatus().getId()) {
            return false;
        }
        if(RequestStatusConstants.OPEN_STATUS == bid.getStatus().getId() || RequestStatusConstants.REVISED_STATUS == bid.getStatus().getId()) {
            if(updateOtherBids(bid.getId(), request.getId(), onHoldStatus)) {
                request.setStatus(initialApprovalStatus);
                request.setUpdateUserId(0L);
                bid.setStatus(initialApprovalStatus);
                bid.setUpdateUser(request.getUser());
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, IOException.class})
    public boolean approveMultipleBids(BidOrderDto bidOrder, Long customer) {
        try{
            initialApprovalStatus = statusService.getStatus(RequestStatusConstants.INITIAL_APPROVE);
            onHoldStatus = statusService.getStatus(RequestStatusConstants.ONHOLD_STATUS);
            List<Bid> bids = bidRepository.findAllBidsOfIdIn(bidOrder.getBids());
            AtomicBoolean failedTransaction = new AtomicBoolean(false);
            bids.stream()
                    .map(this::applyApproveBidBusinessRule)
                    .toList()
                    .stream()
                    .filter(r -> !r)
                    .findAny()
                    .ifPresent(b -> {
                        failedTransaction.set(true);
                    });
            if(failedTransaction.get()) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return false;
            }
            if(handlePlaceOrder(bidOrder, bids)) {
                return true;
            }
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }catch(Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.info("Error inside BidService.approveMultipleBids: " + e.getMessage());
            return false;
        }
    }

    public boolean handlePlaceOrder(BidOrderDto bidOrder, List<Bid> bids) {
        Long placedOrder = placeOrder(bidOrder);
        if(placedOrder != null) {
            bids.parallelStream().forEach(bid -> bid.setOrder(placedOrder));
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, IOException.class})
    public Long approveBidAndPlaceOrder(BidOrderDto bidOrder, Long customer) {
        log.info("Show the status of transaction in approveBidAndPlaceOrder: " + TransactionSynchronizationManager.isActualTransactionActive());
        initialApprovalStatus = statusService.getStatus(RequestStatusConstants.INITIAL_APPROVE);
        onHoldStatus = statusService.getStatus(RequestStatusConstants.ONHOLD_STATUS);
        try{
            Bid bid = bidRepository.getReferenceById(bidOrder.getBids().get(0));
            if( applyApproveBidBusinessRule(bid)) {
                if(RequestKind.Service.equals(bid.getRequest().getRequestType())) {
                    publisher.publishEvent(new ClaimEvent(bid.getRequest().getId(), initialApprovalStatus, bid.getUpdateUser()));
                }
                Long placedOrder = handlePlaceSingleOrder(bidOrder, customer, bid);
                if(placedOrder != null) {
                    return placedOrder;
                }
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return null;
            }
            return null;
        }catch(Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.info("Error inside BidService: " + e.getMessage());
            return null;
        }
    }

    public Long handlePlaceSingleOrder(BidOrderDto bidOrder, Long customer, Bid bid) {
        if(bidOrder.getCustomer() == null) {
            bidOrder.setCustomer(customer);
        }
        if(bidOrder.getSupplier() == null) {
            bidOrder.setSupplier(bid.getSupplier());
        }
        if(bidOrder.getOrderType() == null){
            bidOrder.setOrderType(OrderType.Bid);
        }
        Long placedOrder = placeOrder(bidOrder);
        if(placedOrder != null) {
            bid.setOrder(placedOrder);
            return placedOrder;
        }else{
            return null;
        }
    }

    @Override
    public List<BidVO> findByJobIdVO(Long jobId, Integer pageNo, Integer pageSize) {
        Pageable page = PageRequest.of(pageNo, pageSize);
        return bidRepository.findByJobIdVO(jobId, page);
    }

    @Override
    public List<BidVO> findByOrderVO(Long orderId, Integer pageNo, Integer pageSize) {
        Pageable page = PageRequest.of(pageNo, pageSize);
        return bidRepository.findByOrderVO(orderId, page);
    }

    @Override
    public Long findRequestUserByBid(Long bidId) {
        return bidRepository.findRequestUserByBid(bidId);
    }

    @Override
    public Long findSupplierUserByBid(Long bidId) {
        return bidRepository.findSupplierUserByBid(bidId);
    }

    @Override
    public List<BidVO> findByJobIdAndSupplier(Long jobId, Long supplier, Integer pageNo, Integer pageSize) {
        Pageable page = PageRequest.of(pageNo, pageSize);
        return bidRepository.findByJobIdAndSupplierVO(jobId, supplier, page);
    }


    private Long placeOrder(BidOrderDto bidOrder) {
        return shopFeign.placeOrder(bidOrder);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, IOException.class})
    public boolean rejectBid(Long bidId, String rejectComments) {
        log.info("Show the status of transaction in rejectBid: " + TransactionSynchronizationManager.isActualTransactionActive());
        openStatus = statusService.getStatus(RequestStatusConstants.OPEN_STATUS);
        rejectedStatus = statusService.getStatus(RequestStatusConstants.REJECTED_STATUS);
        confirmedStatus = statusService.getStatus(RequestStatusConstants.CONFIRMED_STATUS);
        try {
            Bid bid = bidRepository.getReferenceById(bidId);
            if(applyRejectBidBusinessRule(bid, rejectComments)) {
                if(RequestKind.Service.equals(bid.getRequest().getRequestType())) {
                    publisher.publishEvent(new ClaimEvent(bid.getRequest().getId(), confirmedStatus, bid.getUpdateUser()));
                }
                if(handleRejectOrder(bid)) {
                    return true;
                }
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return false;
            }
            return false;
        } catch (Exception e) {
            log.info("Inside BidService.rejectBid: " + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    public boolean handleRejectOrder(Bid bid) {
        Long orderId = bid.getOrder();
        if(orderId != null) {
            if(shopFeign.cancelOrder(new OrderRequest(orderId, null, false))) {
                return true;
            }
            return false;
        }
        return true;
    }

    @Override
    @Transactional
    public boolean rejectMultipleBids(MultipleBids rejectMultipleBids) {
        log.info("Show the status of transaction in rejectMultipleBid: " + TransactionSynchronizationManager.isActualTransactionActive());
        openStatus = statusService.getStatus(RequestStatusConstants.OPEN_STATUS);
        rejectedStatus = statusService.getStatus(RequestStatusConstants.REJECTED_STATUS);
        try {
            List<Bid> bids = bidRepository.findAllBidsOfIdIn(rejectMultipleBids.bids());
            AtomicBoolean failedTransaction = new AtomicBoolean(false);
            bids.stream()
                    .map(bid -> applyRejectBidBusinessRule(bid, ""))
                    .toList()
                    .stream()
                    .filter(r -> !r)
                    .findAny()
                    .ifPresent(b -> {
                        failedTransaction.set(true);
                    });
            if(failedTransaction.get()) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return false;
            }
            if(!rejectMultipleBids.processOrder()) {
                return true;
            }
            if(handleRejectOrder(bids.get(0))) {
                return true;
            }
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }catch(Exception e) {
            log.info("Inside BidService.rejectMultipleBids: " + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }
    public boolean applyRejectBidBusinessRule(Bid bid, String rejectComments) {
        Request request = bid.getRequest();
        if(notValidRequest(request)) {
            return false;
        }
        if(RequestStatusConstants.COMPLETED_STATUS != bid.getStatus().getId() && RequestStatusConstants.CANCELED_STATUS != bid.getStatus().getId() && RequestStatusConstants.REJECTED_STATUS != bid.getStatus().getId()){
            boolean updateOtherBidsStatus = updateOtherBids(bid.getId(), request.getId(), openStatus);
            if(RequestStatusConstants.INITIAL_APPROVE == request.getStatus().getId() || RequestStatusConstants.APPROVED_STATUS == request.getStatus().getId() && updateOtherBidsStatus) {
                request.setStatus(openStatus);
                request.setUpdateUserId(0L);
            }
            bid.setStatus(rejectedStatus);
            bid.setUpdateUser(request.getUser());
            bid.setActionComments(rejectComments);
            log.info("rejected");
            return true;
        }
        log.info("not rejected");
        return false;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, IOException.class})
    public boolean reviseBid(Long bidId, String reviseComments, MultipartFile reviseVoiceNote) {
        log.info("Show the status of transaction in reviseBid: " + TransactionSynchronizationManager.isActualTransactionActive());
        revisionStatus = statusService.getStatus(RequestStatusConstants.REVISION_STATUS);
        try {
            Bid bid = bidRepository.getReferenceById(bidId);
            return applyReviseBidBusinessRule(reviseComments, reviseVoiceNote, bid);
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    public boolean applyReviseBidBusinessRule(String reviseComments, MultipartFile reviseVoiceNote, Bid bid) {
        Request request = bid.getRequest();
        if(RequestStatusConstants.OPEN_STATUS != request.getStatus().getId()) {
            return false;
        }
        if(RequestStatusConstants.OPEN_STATUS == bid.getStatus().getId() || RequestStatusConstants.REVISED_STATUS == bid.getStatus().getId()) {
            bid.setStatus(revisionStatus);
            bid.setUpdateUser(request.getUser());
            bid.setReviseComments(reviseComments);
            if(reviseVoiceNote != null) {
                bid.setReviseVoiceNote(documentService.saveDocument(reviseVoiceNote));
            }
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, IOException.class})
    public boolean completeBid(Long bidId, String completeComments) {
        log.info("Show the status of transaction in completeBid: " + TransactionSynchronizationManager.isActualTransactionActive());
        completedStatus = statusService.getStatus(RequestStatusConstants.COMPLETED_STATUS);
        lostStatus = statusService.getStatus(RequestStatusConstants.LOST_STATUS);
        try{
            Bid bid = bidRepository.getReferenceById(bidId);
            if(applyCompleteBidBusinessRule(bid, completeComments)){
                if(RequestKind.Service.equals(bid.getRequest().getRequestType())) {
                    publisher.publishEvent(new ClaimEvent(bid.getRequest().getId(), completedStatus, bid.getUpdateUser()));
                }
                if(handleCompleteOrder(bid)) {
                    return true;
                }
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return false;
            }
            return false;
        }catch(Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.info("Error inside BidService: " + e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, IOException.class})
    public boolean completeMultipleBids(MultipleBids completeMultipleBids) {
        log.info("Show the status of transaction in completeMultipleBids: " + TransactionSynchronizationManager.isActualTransactionActive());
        completedStatus = statusService.getStatus(RequestStatusConstants.COMPLETED_STATUS);
        lostStatus = statusService.getStatus(RequestStatusConstants.LOST_STATUS);
        try{
            List<Bid> bids = bidRepository.findAllBidsOfIdIn(completeMultipleBids.bids());
            AtomicBoolean failedTransaction = new AtomicBoolean(false);
            bids.stream()
                    .map(bid -> applyCompleteBidBusinessRule(bid, ""))
                    .toList()
                    .stream()
                    .filter(r -> !r)
                    .findAny()
                    .ifPresent(b -> {
                        failedTransaction.set(true);
                    });
            if(failedTransaction.get()) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return false;
            }
            return true;
        }catch(Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.info("Error inside BidService.completeMultipleBids: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<BidClaimVO> findByClaimIdAndSupplier(Long claimId, Long supplier, Integer pageNo, Integer pageSize) {
        Pageable page = PageRequest.of(pageNo, pageSize);
        return bidRepository.findByClaimIdAndSupplierVO(claimId, supplier, page);
    }

    @Override
    public List<BidClaimVO> findByClaimIdVO(Long claimId, Integer pageNo, Integer pageSize) {
        Pageable page = PageRequest.of(pageNo, pageSize);
        return bidRepository.findByClaimIdVO(claimId, page);
    }

    public boolean handleCompleteOrder(Bid bid) {
        Long orderId = bid.getOrder();
        if(orderId != null) {
            if(shopFeign.readyForShippingOrder(new OrderRequest(orderId, bid.getSupplier(), false))) {
                return true;
            }
            return false;
        }
        return true;
    }

    public boolean applyCompleteBidBusinessRule(Bid bid, String completeComments) {
        Request request = bid.getRequest();
        if (RequestStatusConstants.APPROVED_STATUS != request.getStatus().getId()) {
            return false;
        }
        if (RequestStatusConstants.APPROVED_STATUS == bid.getStatus().getId()) {
            if (updateOtherBids(bid.getId(), request.getId(), lostStatus)) {
                request.setStatus(completedStatus);
                request.setUpdateUserId(0L);
                bid.setStatus(completedStatus);
                bid.setUpdateUser(request.getUser());
                bid.setActionComments(completeComments);
                return true;
            }
        }
        return false;
    }


    @Override
    @Transactional(rollbackFor = {Exception.class, IOException.class})
    public boolean acceptBid(Long bidId) {
        log.info("Show the status of transaction in acceptBid: " + TransactionSynchronizationManager.isActualTransactionActive());
        approvedStatus = statusService.getStatus(RequestStatusConstants.APPROVED_STATUS);
        onHoldStatus = statusService.getStatus(RequestStatusConstants.ONHOLD_STATUS);
        try {
            Bid bid = bidRepository.getReferenceById(bidId);
            if(applyAcceptBidBusinessRule(bid)) {
                if(RequestKind.Service.equals(bid.getRequest().getRequestType())) {
                    publisher.publishEvent(new ApproveClaimEvent(bid.getRequest().getId(), approvedStatus, bid.getCreateUser(), bid.getSupplier()));
                }
                if(handleAcceptOrder(bid)) {
                    return true;
                }
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return false;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, IOException.class})
    public boolean acceptMultipleBids(MultipleBids acceptMultipleBids) {
        log.info("Show the status of transaction in acceptMultipleBids: " + TransactionSynchronizationManager.isActualTransactionActive());
        approvedStatus = statusService.getStatus(RequestStatusConstants.APPROVED_STATUS);
        onHoldStatus = statusService.getStatus(RequestStatusConstants.ONHOLD_STATUS);
        try{
            List<Bid> bids = bidRepository.findAllBidsOfIdIn(acceptMultipleBids.bids());
            AtomicBoolean failedTransaction = new AtomicBoolean(false);
            bids.stream()
                    .map(this::applyAcceptBidBusinessRule)
                    .toList()
                    .stream()
                    .filter(r -> !r)
                    .findAny()
                    .ifPresent(b -> {
                        failedTransaction.set(true);
                    });
            if(failedTransaction.get()) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return false;
            }
            return true;
        }catch(Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.info("Error inside BidService.acceptMultipleBids: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Long> findBidIdListOfOrder(Long orderId) {
        return bidRepository.findBidIdListOfOrder(orderId);
    }

    public boolean handleAcceptOrder(Bid bid) {
        Long orderId = bid.getOrder();
        if(orderId != null) {
            if(shopFeign.acceptOrder(new OrderRequest(orderId, bid.getSupplier(), false))) {
                return true;
            }
            return false;
        }
        return true;
    }

    public boolean applyAcceptBidBusinessRule(Bid bid) {
        Request request = bid.getRequest();
        if (RequestStatusConstants.INITIAL_APPROVE != request.getStatus().getId()) {
            return false;
        }
        if (RequestStatusConstants.INITIAL_APPROVE == bid.getStatus().getId()) {
            if (updateOtherBids(bid.getId(), request.getId(), onHoldStatus)) {
                request.setStatus(approvedStatus);
                request.setUpdateUserId(0L);
                bid.setStatus(approvedStatus);
                bid.setUpdateUser(bid.getSupplier());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean updateOtherBids(Long bidId, Long requestId, Status newStatus) {
        List<Bid> otherBids = findByRequestId(requestId);
        if(otherBids == null || otherBids.isEmpty()) {
            log.info("There is no Bids in this Request");
            return true;
        }
        otherBids.remove(bidRepository.getReferenceById(bidId));
        otherBids = otherBids.stream().filter(bid1 -> (RequestStatusConstants.CANCELED_STATUS != bid1.getStatus().getId() && RequestStatusConstants.REJECTED_STATUS != bid1.getStatus().getId())).collect(Collectors.toList());
        BidStatusDto bidStatusDto = new BidStatusDto();
        if(otherBids.isEmpty()) {
            return true;
        }
        for(Bid myBid : otherBids) {
            myBid.setStatus(newStatus);
            myBid.setUpdateUser(0L);
            bidStatusDto.setId(myBid.getId());
            bidStatusDto.setStatus(newStatus);
        }
        return true;
    }

    @Override
    public boolean updateAllBids(Long requestId, Status newStatus) {
        try{
            List<Bid> otherBids = findByRequestId(requestId);
            if(otherBids == null || otherBids.isEmpty()) {
                log.info("There is no Bids in this Request");
                return true;
            }
            otherBids = otherBids.stream().filter(bid1 -> RequestStatusConstants.CANCELED_STATUS != bid1.getStatus().getId()).collect(Collectors.toList());
            BidStatusDto bidStatusDto = new BidStatusDto();
            for(Bid myBid : otherBids) {
                myBid.setStatus(newStatus);
                myBid.setUpdateUser(0L);
                bidStatusDto.setId(myBid.getId());
                bidStatusDto.setStatus(newStatus);
            }
            return true;
        }catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countByRequestId(Long requestId) {
        return bidRepository.countByRequestId(requestId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countRejectedByRequestId(Long requestId) {
        return bidRepository.countRejectedByRequestId(requestId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countBySupplierId(Long supplierId) {
        return bidRepository.countBySupplierId(supplierId);
    }

    @Override
    public boolean existsById(Long bidId) {
        return bidRepository.existsById(bidId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BidVO> findHistoryBySupplierId(Long supplierId, Integer pageNo, Integer pageSize) {
        Pageable page = PageRequest.of(pageNo, pageSize);
        return bidRepository.findHistoryBySupplierId(supplierId, page);
    }

    @Override
    public List<Bid> findAllByRequestId(Long id) {
        return bidRepository.findAllByRequestId(id);
    }

    @Override
    public List<Bid> findBySupplierAndRequest(Long supplierId, Long requestId) {
        return bidRepository.findBySupplierAndRequest(supplierId, requestId);
    }


    public boolean validateNotExceedsNoOfAllowedBids(Long supplier, Integer noOfSubmittedBids) {
        GeneralBusinessRule customRule = businessRulesService.findByPrincipleAndRuleTypeAndRuleNameAndRefId(BusinessPrinciple.Supplier, BusinessRuleType.Custom,"MaxAllowedBids", supplier);
        if(customRule != null && noOfSubmittedBids >= Integer.parseInt(customRule.getRuleValue())) {
            return true;
        }
        GeneralBusinessRule rule = businessRulesService.findByPrincipleAndRuleTypeAndRuleName(BusinessPrinciple.Supplier, BusinessRuleType.General, "MaxAllowedBids");
        if(customRule == null && rule != null && noOfSubmittedBids >= Integer.parseInt(rule.getRuleValue())) {
            return true;
        }
        return false;
    }
}
