package com.xgarage.app.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.xgarage.app.dto.*;
import com.xgarage.app.event.NotificationEvent;
import com.xgarage.app.feign.KernelFeign;
import com.xgarage.app.model.*;
import com.xgarage.app.service.BidService;
import com.xgarage.app.service.PartTypeService;
import com.xgarage.app.service.RequestService;
import com.xgarage.app.utils.OperationCode;
import com.xgarage.app.utils.TenantTypeConstants;
import com.xgarage.app.utils.UserHelperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/core/api/v1/bid")
@Slf4j
public class BidController {

    @Autowired
    private BidService bidService;
    @Autowired
    private RequestService requestService;

    @Autowired
    private UserHelperService userHelper;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private PartTypeService partTypeService;

    @Autowired private KernelFeign kernelFeign;

    @Autowired private OperationCode operationCode;


    @GetMapping("/all")
    public ResponseEntity<?> getAllBid(@RequestParam(defaultValue = "0") Integer pageNo,
                                       @RequestParam(defaultValue = "50") Integer pageSize){
        try{
            List<BidVO> bids = bidService.findAllBids(pageNo, pageSize);
            if(bids != null) {
                return ResponseEntity.ok().body(bids);
            }
            return operationCode.craftResponse("bid.getall.notfound", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("getAllBid Error:" + e.getMessage());
            return operationCode.craftResponse("bid.getall.badrequest", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/supplierHistory")
    public ResponseEntity<?> getHistoryBySupplierId(@RequestParam(defaultValue = "0") Integer pageNo,
                                                    @RequestParam(defaultValue = "50") Integer pageSize){
        try{
            Long supplier = userHelper.getAuthenticatedSupplierId();
            if(supplier == null) {
                return operationCode.craftResponse("bid.gethistorybysupplier.suppliernotfound", HttpStatus.NOT_FOUND);
            }
            List<BidVO> bids = bidService.findHistoryBySupplierId(supplier, pageNo, pageSize);
            if(bids != null) {
                return ResponseEntity.ok().body(bids);
            }
            return operationCode.craftResponse("bid.gethistorybysupplier.bidnotfound", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("getBidHistoryBySupplierId Error:" + e.getMessage());
            return operationCode.craftResponse("bid.gethistorybysupplier.badrequest", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/id/{bidId}")
    public ResponseEntity<?> getBidById(@PathVariable("bidId") Long bidId) {
        try{
            BidVO bid = bidService.findBidDtoById(bidId);
            if(bid != null) {
                return ResponseEntity.ok().body(bid);
            }
            return operationCode.craftResponse("bid.getbidbyid.notfound", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("getBidById Error:" + e.getMessage());
            return operationCode.craftResponse("bid.getall.badrequest", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/request/{requestId}")
    public ResponseEntity<?> findBidByRequestId(@PathVariable("requestId") Long requestId, @RequestParam(defaultValue = "0") Integer pageNo,
                                                @RequestParam(defaultValue = "50") Integer pageSize){
        try{
            if(!requestService.existsById(requestId)) {
                return operationCode.craftResponse("bid.getbidbyrequestid.requestnotfound", HttpStatus.NOT_FOUND);
            }
            List<BidVO> bids = bidService.findByRequestIdVO(requestId, pageNo, pageSize);
            if(bids != null && !bids.isEmpty()) {
                return ResponseEntity.ok().body(bids);
            }
            return operationCode.craftResponse("bid.getbyrequest.notfound", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("findBidByRequestId Error:" + e.getMessage());
            return operationCode.craftResponse("bid.getall.notfound", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<?> findBidsOfJob(@PathVariable("jobId") Long jobId, @RequestParam(defaultValue = "0") Integer pageNo,
                                                @RequestParam(defaultValue = "50") Integer pageSize){
        try{
            Long tenantType = userHelper.getTenantType();
            List<BidVO> bids;
            if(TenantTypeConstants.Supplier.equals(tenantType)) {
                Long supplier = userHelper.getTenant();
                bids = bidService.findByJobIdAndSupplier(jobId, supplier, pageNo, pageSize);
            }else{
                bids = bidService.findByJobIdVO(jobId, pageNo, pageSize);
            }
            if(bids != null) {
                return ResponseEntity.ok().body(bids);
            }
            return operationCode.craftResponse("bid.getbyjob.notfound", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("findBidByRequestId Error:" + e.getMessage());
            return operationCode.craftResponse("bid.getall.notfound", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/claim/{claimId}")
    public ResponseEntity<?> findBidsOfClaim(@PathVariable("claimId") Long claimId, @RequestParam(defaultValue = "0") Integer pageNo,
                                           @RequestParam(defaultValue = "50") Integer pageSize){
        try{
            Long tenantType = userHelper.getTenantType();
            Long supplier = userHelper.getTenant();
            List<BidClaimVO> bids;
            if(TenantTypeConstants.Garage.equals(tenantType)) {
                bids = bidService.findByClaimIdAndSupplier(claimId, supplier, pageNo, pageSize);
            }else{
                bids = bidService.findByClaimIdVO(claimId, pageNo, pageSize);
            }
            if(bids != null) {
                return ResponseEntity.ok().body(bids);
            }
            return operationCode.craftResponse("bid.getall.notfound", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("findBidByRequestId Error:" + e.getMessage());
            return operationCode.craftResponse("bid.getall.notfound", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> findBidsOfOrder(@PathVariable("orderId") Long orderId, @RequestParam(defaultValue = "0") Integer pageNo,
                                           @RequestParam(defaultValue = "50") Integer pageSize){
        try{
            List<BidVO> bids = bidService.findByOrderVO(orderId, pageNo, pageSize);
            if(bids != null) {
                return ResponseEntity.ok().body(bids);
            }
            return operationCode.craftResponse("bid.getbyjob.notfound", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("findBidByRequestId Error:" + e.getMessage());
            return operationCode.craftResponse("bid.getall.notfound", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/order/id/{orderId}")
    public ResponseEntity<?> findBidIdListOfOrder(@PathVariable("orderId") Long orderId){
        try{
            List<Long> bids = bidService.findBidIdListOfOrder(orderId);
            if(bids != null) {
                return ResponseEntity.ok().body(bids);
            }
            return operationCode.craftResponse("bid.getbyjob.notfound", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("findBidByRequestId Error:" + e.getMessage());
            return operationCode.craftResponse("bid.getall.notfound", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/notInterestedSuppliers/{requestId}")
    public ResponseEntity<?> findNotInterestedSuppliersInRequest(@PathVariable("requestId") Long requestId, @RequestParam(defaultValue = "0") Integer pageNo,
                                                @RequestParam(defaultValue = "50") Integer pageSize){
        try{
            if(!requestService.existsById(requestId)) {
                return operationCode.craftResponse("bid.getbidbyrequestid.requestnotfound", HttpStatus.NOT_FOUND);
            }
            List<SupplierVO> suppliers = requestService.findNotInterestedSuppliersInRequest(requestId, pageNo, pageSize);
            if(suppliers != null) {
                return ResponseEntity.ok().body(suppliers);
            }
            return operationCode.craftResponse("bid.getbyrequest.notfound", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("findBidByRequestId Error:" + e.getMessage());
            return operationCode.craftResponse("bid.getall.notfound", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/supplier")
    public ResponseEntity<?> findBidBySupplierId(@RequestParam(defaultValue = "0") Integer pageNo,
                                                 @RequestParam(defaultValue = "50") Integer pageSize){
        try{
            Long supplier = userHelper.getAuthenticatedSupplierId();
            if(supplier == null) {
                return operationCode.craftResponse("bid.gethistorybysupplier.suppliernotfound", HttpStatus.NOT_FOUND);
            }
            List<BidVO> bids = bidService.findBySupplierId(supplier, pageNo, pageSize);
            if(bids != null) {
                return ResponseEntity.ok().body(bids);
            }
            return operationCode.craftResponse("bid.getbysupplier.notfound", HttpStatus.NOT_FOUND);
        }catch(Exception e) {
            log.info("findBidBySupplierId Error:" + e.getMessage());
            return operationCode.craftResponse("bid.getall.notfound", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/acceptBid/{bidId}")
    public ResponseEntity<?> acceptBid(@PathVariable("bidId") Long bidId) {
        String keyAcceptBidTitle = "Accept Bid";
        String keyAcceptBidMessage = "Bid #" + bidId + " Has Been Accepted.";
        try{
            if(!bidService.existsById(bidId)) {
                return operationCode.craftResponse("bid.getbidbyid.notfound", HttpStatus.NOT_FOUND);
            }
            boolean acceptedBid = bidService.acceptBid(bidId);
            if(acceptedBid) {
                Long user = bidService.findRequestUserByBid(bidId);
                NotificationEvent notificationEvent = new NotificationEvent(PrincipleType.User.name(), Collections.singletonList(user), NotificationType.Bid.name(), bidId, keyAcceptBidTitle, keyAcceptBidMessage, "Private");
                kernelFeign.sendPushNotification(notificationEvent);
//                streamBridge.send("notification-out-0", notificationEvent);
                return operationCode.craftResponse("bid.acceptbid.ok", HttpStatus.OK);
            }
            return operationCode.craftResponse("bid.acceptbid.badrequest", HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            log.info("acceptBid Error:" + e.getMessage());
            return operationCode.craftResponse("bid.acceptbid.forbiddent", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/reviseBid/{requestId}/{bidId}")
    public ResponseEntity<?> reviseBid(@PathVariable("requestId") Long requestId, @PathVariable("bidId") Long bidId, @RequestParam(value = "comments", required = false) String reviseComments, @RequestParam(value = "voiceNote", required = false) MultipartFile reviseVoiceNote) {
        String keyReviseBidTitle = "Revise Bid";
        String keyReviseBidMessage = "Bid #" + bidId + " Has Been Submitted for Revision.";
        try{
            ResponseEntity<?> result = checkRequestParameters(requestId, bidId);
            if(result != null) {
                return result;
            }
            boolean revisedBid = bidService.reviseBid(bidId, reviseComments, reviseVoiceNote);
            if(revisedBid) {
                Long supplierUser = bidService.findSupplierUserByBid(bidId);
                NotificationEvent notificationEvent = new NotificationEvent(PrincipleType.Supplier.name(), Collections.singletonList(supplierUser), NotificationType.Bid.name(), bidId, keyReviseBidTitle, keyReviseBidMessage, "Private");
                kernelFeign.sendPushNotification(notificationEvent);
//                streamBridge.send("notify-out-0", notificationEvent);
                return operationCode.craftResponse("bid.revisebid.ok", HttpStatus.OK);
            }
            return operationCode.craftResponse("bid.revisebid.badrequest", HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            log.info("acceptBid Error:" + e.getMessage());
            return operationCode.craftResponse("bid.revisebid.forbidden", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/completeBid/{requestId}/{bidId}")
    public ResponseEntity<?> completeBid(@PathVariable("requestId") Long requestId, @PathVariable("bidId") Long bidId, @RequestBody(required = false) ActionComments completeComments) {
        String keyCompleteBidTitle = "Collect Bid";
        String keyCompleteBidMessage = "Bid #" + bidId + " Has Been Collected.";
        try{
            ResponseEntity<?> result = checkRequestParameters(requestId, bidId);
            if(result != null) {
                return result;
            }
            boolean completedBid = bidService.completeBid(bidId, (completeComments == null ? null : completeComments.getComments()));
            if(completedBid) {
                Long supplierUser = bidService.findSupplierUserByBid(bidId);
                NotificationEvent notificationEvent = new NotificationEvent(PrincipleType.Supplier.name(), Collections.singletonList(supplierUser), NotificationType.Bid.name(), bidId, keyCompleteBidTitle, keyCompleteBidMessage, "Private");
                kernelFeign.sendPushNotification(notificationEvent);
//                streamBridge.send("notify-out-0", notificationEvent);
                return operationCode.craftResponse("bid.completebid.ok", HttpStatus.OK);
            }
            return operationCode.craftResponse("bid.completebid.badrequest", HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            log.info("acceptBid Error:" + e.getMessage());
            return operationCode.craftResponse("bid.completebid.forbidden", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/completeBid/{bidId}")
    public ResponseEntity<?> completeBidAndReadyForShippingOrder(@PathVariable("bidId") Long bidId, @RequestBody(required = false) ActionComments completeComments) {
        String keyCompleteBidTitle = "Ready for Shipping Bid";
        String keyCompleteBidMessage = "Bid #" + bidId + " Has Ready for Shipping.";
        try{
            if(!bidService.existsById(bidId)) {
                return operationCode.craftResponse("bid.getbidbyid.notfound", HttpStatus.NOT_FOUND);
            }
            boolean completedBid = bidService.completeBid(bidId, (completeComments == null ? null : completeComments.getComments()));
            if(completedBid) {
                Long user = bidService.findRequestUserByBid(bidId);
                NotificationEvent notificationEvent = new NotificationEvent(PrincipleType.User.name(), Collections.singletonList(user), NotificationType.Bid.name(), bidId, keyCompleteBidTitle, keyCompleteBidMessage, "Private");
                kernelFeign.sendPushNotification(notificationEvent);
//                streamBridge.send("notify-out-0", notificationEvent);
                return operationCode.craftResponse("bid.completebid.ok", HttpStatus.OK);
            }
            return operationCode.craftResponse("bid.completebid.badrequest", HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            log.info("acceptBid Error:" + e.getMessage());
            return operationCode.craftResponse("bid.completebid.forbidden", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveBid(@RequestParam(value = "bidBody", required = false) String stringBid, @RequestPart(value = "bidImages", required = false) List<MultipartFile> bidImages, @RequestPart(value = "voiceNote", required = false) MultipartFile voiceNote) {
        String keyNewBidTitle = "New Bid";
        String keyNewBidMessage = "New Bid Has Been Submitted #";
        try{
            Bid bid = new ObjectMapper().readValue(stringBid, Bid.class);
            if(!requestService.existsById(bid.getRequest().getId())) {
                return operationCode.craftResponse("bid.getbidbyrequestid.requestnotfound", HttpStatus.NOT_FOUND);
            }
            Long supplier = userHelper.getAuthenticatedSupplierId();
            if(supplier == null) {
                return operationCode.craftResponse("bid.gethistorybysupplier.suppliernotfound", HttpStatus.NOT_FOUND);
            }
            bid.setSupplier(supplier);
            Long createdUser = userHelper.getAuthenticatedUser();
            if(createdUser != null) {
                bid.setCreateUser(createdUser);
            }
            Request request = requestService.findRequestById(bid.getRequest().getId());
            Bid savedBid = bidService.saveBid(bid, bidImages, voiceNote, request);
            if(savedBid != null && OperationCode.OUT_OF_ALLOWED_BIDS_CODE.equals(savedBid.getOperationCode())) {
                return operationCode.craftResponse("bid.supplier.exceedednoofbids", HttpStatus.BAD_REQUEST);
            }
            if(savedBid != null && OperationCode.Cancelled_Request.equals(savedBid.getOperationCode())) {
                return operationCode.craftResponse("bid.supplier.cancelledrequest", HttpStatus.BAD_REQUEST);
            }
            if(savedBid != null && OperationCode.Completed_Request.equals(savedBid.getOperationCode())) {
                return operationCode.craftResponse("bid.supplier.completedrequest", HttpStatus.BAD_REQUEST);
            }
            if(savedBid != null && OperationCode.SUCCESS_CODE.equals(savedBid.getOperationCode())) {
                Long user = bidService.findRequestUserByBid(savedBid.getId());
                NotificationEvent notificationEvent = new NotificationEvent(PrincipleType.User.name(), Collections.singletonList(user), NotificationType.Bid.name(), savedBid.getId(), keyNewBidTitle, keyNewBidMessage + savedBid.getId(), "Private");
                kernelFeign.sendPushNotification(notificationEvent);
//                streamBridge.send("notify-out-0", notificationEvent);
                return ResponseEntity.ok(savedBid.getId());
            }
            return operationCode.craftResponse("operation.bid.badrequest", HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            log.info("saveBid Error:" + e.getMessage());
            return operationCode.craftResponse("operation.bid.forbidden", HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateBid(@RequestParam(value = "bidBody", required = false) String stringBid , @RequestParam(value = "bidImages", required = false) List<MultipartFile> bidImages, @RequestParam(value = "voiceNote", required = false) MultipartFile voiceNote) {
        String keyUpdateBidTitle = "Update Bid";
        String keyUpdateBidMessage = "";
        Long updatedUser = userHelper.getAuthenticatedUser();
        try{
            Bid bid = new ObjectMapper().readValue(stringBid, Bid.class);
            bid.setUpdateUser(userHelper.getAuthenticatedUser());
            if(!bidService.existsById(bid.getId())) {
                return operationCode.craftResponse("bid.getbidbyid.notfound", HttpStatus.NOT_FOUND);
            }
            if(!requestService.existsById(bid.getRequest().getId())) {
                return operationCode.craftResponse("operation.request.notfound", HttpStatus.NOT_FOUND);
            }
            if(bidService.updateBid(bid, bidImages, voiceNote, updatedUser)) {
                keyUpdateBidMessage = "Bid #" + bid.getId() + " Has Been Updated.";
                Long user = bidService.findRequestUserByBid(bid.getId());
                NotificationEvent notificationEvent = new NotificationEvent(PrincipleType.User.name(), Collections.singletonList(user), NotificationType.Bid.name(), bid.getId(), keyUpdateBidTitle, keyUpdateBidMessage, "Private");
                kernelFeign.sendPushNotification(notificationEvent);
//                streamBridge.send("notify-out-0", notificationEvent);
                return operationCode.craftResponse("operation.bid.update", HttpStatus.OK);
            }
            return operationCode.craftResponse("bid.updatebid.badrequest", HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            log.info("updateBid Error:" + e.getMessage());
            return operationCode.craftResponse("bid.updatebid.forbidden", HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/rejectBid/{requestId}/{bidId}")
    public ResponseEntity<?> rejectBid(@PathVariable("requestId") Long requestId, @PathVariable("bidId") Long bidId, @RequestBody(required = false) ActionComments rejectComments) {
        String keyRejectBidTitle = "Reject Bid";
        String keyRejectBidMessage = "Bid #" + bidId + " Has Been Rejected.";
        try{
            log.info("Inside rejectBid, requestId, bidId: " + requestId + " - " + bidId);
            ResponseEntity<?> result = checkRequestParameters(requestId, bidId);
            if(result != null) {
                return result;
            }
            boolean rejectedBid = bidService.rejectBid(bidId, (rejectComments == null ? null : rejectComments.getComments()));
            if(rejectedBid) {
                Long supplierUser = bidService.findSupplierUserByBid(bidId);
                NotificationEvent notificationEvent = new NotificationEvent(PrincipleType.Supplier.name(), Collections.singletonList(supplierUser), NotificationType.Bid.name(), bidId, keyRejectBidTitle, keyRejectBidMessage, "Private");
                kernelFeign.sendPushNotification(notificationEvent);
//                streamBridge.send("notify-out-0", notificationEvent);
                return operationCode.craftResponse("bid.rejectbid.ok", HttpStatus.OK);
            }
            return operationCode.craftResponse("bid.rejectbid.badrequest", HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            log.info("rejectBid Error:" + e.getMessage());
            return operationCode.craftResponse("bid.rejectbid.forbidden", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/cancelBid/{bidId}")
    public ResponseEntity<?> cancelBid(@PathVariable("bidId") Long bidId, @RequestBody(required = false) ActionComments cancelComments) {
        String keyCancelBidTitle = "Cancel Bid";
        String keyCanceledBidMessage = "Bid #" + bidId + " Has Been Cancelled.";
        try{
            if(!bidService.existsById(bidId)) {
                return operationCode.craftResponse("id.getbidbyid.notfound", HttpStatus.NOT_FOUND);
            }
            boolean canceledBid = bidService.cancelBid(bidId, (cancelComments == null ? null : cancelComments.getComments()));
            if(canceledBid) {
                Long user = bidService.findRequestUserByBid(bidId);
                NotificationEvent notificationEvent = new NotificationEvent(PrincipleType.User.name(), Collections.singletonList(user), NotificationType.Bid.name(), bidId, keyCancelBidTitle, keyCanceledBidMessage, "Private");
                kernelFeign.sendPushNotification(notificationEvent);
//                streamBridge.send("notify-out-0", notificationEvent);
                return operationCode.craftResponse("bid.cancelbid.ok", HttpStatus.OK);
            }
            return operationCode.craftResponse("bid.cancelbid.badrequest", HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            log.info("cancelBid Error:" + e.getMessage());
            return operationCode.craftResponse("bid.cancelbid.forbidden", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/approveBid/{requestId}/{bidId}")
    public ResponseEntity<?> approveBid(@PathVariable("requestId") Long requestId, @PathVariable("bidId") Long bidId) {
        String keyApproveBidTitle = "Approve Bid";
        String keyApproveBidMessage = "Bid #" + bidId + " Has Been Approved.";
        try{
            ResponseEntity<?> result = checkRequestParameters(requestId, bidId);
            if(result != null) {
                return result;
            }
            boolean approveBid = bidService.approveBid(bidId);
            if(approveBid) {
                Long supplierUser = bidService.findSupplierUserByBid(bidId);
                NotificationEvent notificationEvent = new NotificationEvent(PrincipleType.Supplier.name(), Collections.singletonList(supplierUser), NotificationType.Bid.name(), bidId, keyApproveBidTitle, keyApproveBidMessage, "Private");
                kernelFeign.sendPushNotification(notificationEvent);
//                streamBridge.send("notify-out-0", notificationEvent);
                return operationCode.craftResponse("bid.approvebid.ok", HttpStatus.OK);
            }
            return operationCode.craftResponse("bid.approvebid.badrequest", HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            log.info("approveBid Error:" + e.getMessage());
            return operationCode.craftResponse("bid.approvebid.forbidden", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/approveBid")
    public ResponseEntity<?> approveBidAndPlaceOrder(@RequestBody BidOrderDto bidOrder) {
        Long bidId = bidOrder.getBids().get(0);
        String keyApproveBidTitle = "Approve Bid";
        String keyApproveBidMessage = "Bid #" + bidId + " Has Been Approved.";
        Long customer = userHelper.getAuthenticatedUser();
        try{
            Long orderId = bidService.approveBidAndPlaceOrder(bidOrder, customer);
            if(orderId != null) {
                Long supplierUser = bidService.findSupplierUserByBid(bidId);
                NotificationEvent notificationEvent = new NotificationEvent(PrincipleType.Supplier.name(), Collections.singletonList(supplierUser), NotificationType.Bid.name(), bidId, keyApproveBidTitle, keyApproveBidMessage, "Private");
                kernelFeign.sendPushNotification(notificationEvent);
//                streamBridge.send("notify-out-0", notificationEvent);
                return ResponseEntity.ok().body(orderId);
            }
            return operationCode.craftResponse("bid.approvebid.badrequest", HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            log.info("approveBid Error:" + e.getMessage());
            return operationCode.craftResponse("bid.approvebid.forbidden", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/approveBid/multiple")
    public ResponseEntity<?> approveMultipleBids(@RequestBody BidOrderDto bidOrder) {
        try{
            Long customer = userHelper.getAuthenticatedUser();
            if(bidOrder.getBids().size() == 1) {
                return ResponseEntity.ok().body((bidService.approveBidAndPlaceOrder(bidOrder, customer) != null));
            }
            return ResponseEntity.ok().body(bidService.approveMultipleBids(bidOrder, customer));
        }catch(Exception e) {
            log.info("approveMultipleBids Error:" + e.getMessage());
            return operationCode.craftResponse("operation.forbidden", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/completeBid/multiple")
    public ResponseEntity<?> completeMultipleBids(@RequestBody MultipleBids completeMultipleBids) {
        try{
            return ResponseEntity.ok().body(bidService.completeMultipleBids(completeMultipleBids));
        }catch(Exception e) {
            log.info("completeMultipleBids Error:" + e.getMessage());
            return operationCode.craftResponse("operation.forbidden", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/acceptBid/multiple")
    public ResponseEntity<?> acceptMultipleBids(@RequestBody MultipleBids acceptMultipleBids) {
        try{
            return ResponseEntity.ok().body(bidService.acceptMultipleBids(acceptMultipleBids));
        }catch(Exception e) {
            log.info("acceptMultipleBids Error:" + e.getMessage());
            return operationCode.craftResponse("operation.forbidden", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/rejectBid/multiple")
    public ResponseEntity<?> rejectMultipleBids(@RequestBody MultipleBids rejectMultipleBids) {
        try{
            return ResponseEntity.ok().body(bidService.rejectMultipleBids(rejectMultipleBids));
        }catch(Exception e) {
            log.info("cancelMultipleBids Error:" + e.getMessage());
            return operationCode.craftResponse("operation.forbidden", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/cancelBid/multiple")
    public ResponseEntity<?> cancelMultipleBids(@RequestBody MultipleBids cancelMultipleBids) {
        try{
            return ResponseEntity.ok().body(bidService.cancelMultipleBids(cancelMultipleBids));
        }catch(Exception e) {
            log.info("cancelMultipleBids Error:" + e.getMessage());
            return operationCode.craftResponse("operation.forbidden", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<?> checkRequestParameters(Long requestId, Long bidId) {
        if(!requestService.existsById(requestId)) {
            return operationCode.craftResponse("bid.getbidbyrequestid.requestnotfound", HttpStatus.NOT_FOUND);
        }
        if(bidService.countByRequestId(requestId) == 0) {
            return operationCode.craftResponse("bid.getbyrequest.notfound", HttpStatus.BAD_REQUEST);
        }
        if(!bidService.existsById(bidId)) {
            return operationCode.craftResponse("bid.getbidbyid.notfound", HttpStatus.NOT_FOUND);
        }
        return null;
    }

}
