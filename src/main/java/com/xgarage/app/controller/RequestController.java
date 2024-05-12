package com.xgarage.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xgarage.app.dto.MessageResponse;
import com.xgarage.app.dto.RequestUserStatsDto;
import com.xgarage.app.dto.RequestVO;
import com.xgarage.app.event.NotificationEvent;
import com.xgarage.app.feign.KernelFeign;
import com.xgarage.app.model.*;
import com.xgarage.app.dto.SupplierDto;
import com.xgarage.app.service.BidService;
import com.xgarage.app.service.RequestService;
import com.xgarage.app.service.SupplierService;
import com.xgarage.app.utils.OperationCode;
import com.xgarage.app.utils.UserHelperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.rmi.NoSuchObjectException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/core/api/v1/request")
@Slf4j
public class RequestController {

    @Autowired
    private RequestService requestService;

    @Autowired
    private BidService bidService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private UserHelperService userHelper;

    @Autowired private KernelFeign kernelFeign;

    @Autowired private OperationCode operationCode;



    @GetMapping("/all")
    public ResponseEntity<?> getAllRequests(@RequestParam(defaultValue = "0") Integer pageNo,
                                             @RequestParam(defaultValue = "50") Integer pageSize){
        try{
            List<RequestVO> requests = requestService.findAllRequests(pageNo, pageSize);
            if(requests == null) {
                return operationCode.craftResponse("operation.request.notfound", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(requests);
        }catch(Exception e) {
            log.info("getAllRequests Error:" + e.getMessage());
            return operationCode.craftResponse("operation.exception", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/claim/{claimId}")
    public ResponseEntity<?> getAllRequestsOfClaim(@PathVariable("claimId") Long claimId, @RequestParam(defaultValue = "0") Integer pageNo,
                                            @RequestParam(defaultValue = "50") Integer pageSize){
        try{
            List<RequestVO> requests = requestService.findAllClaimRequests(pageNo, pageSize, claimId);
            if(requests == null) {
                return new ResponseEntity<>("Requests Not Found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(requests);
        }catch(Exception e) {
            log.info("getAllRequests Error:" + e.getMessage());
            return new ResponseEntity("Error Fetching Requests", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<?> getAllRequestsOfJob(@PathVariable("jobId") Long jobId, @RequestParam(defaultValue = "0") Integer pageNo,
                                                   @RequestParam(defaultValue = "50") Integer pageSize){
        try{
            List<Request> requests = requestService.findAllJobRequests(pageNo, pageSize, jobId);
            if(requests == null) {
                return new ResponseEntity<>("Requests Not Found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(requests);
        }catch(Exception e) {
            log.info("getAllRequests Error:" + e.getMessage());
            return new ResponseEntity("Error Fetching Requests", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/supplier")
    public ResponseEntity<?> getAllRequestsForSupplier(@RequestParam(defaultValue = "0") Integer pageNo,
                                                       @RequestParam(defaultValue = "50") Integer pageSize){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try{
            Long supplier = userHelper.getAuthenticatedSupplierId();
            if(supplier == null) {
                return operationCode.craftResponse("bid.gethistorybysupplier.suppliernotfound", HttpStatus.NOT_FOUND);
            }
            List<RequestVO> requests = requestService.findInterestedRequestsForSupplier(supplier, pageNo, pageSize);
            if(requests == null) {
                stopWatch.stop();
                log.info("Time Elapsed in findInterestedRequestsForSupplier in Controller: " + stopWatch.getTotalTimeSeconds());
                return new ResponseEntity<>("Requests Not Found", HttpStatus.NOT_FOUND);
            }
            stopWatch.stop();
            log.info("Time Elapsed in findInterestedRequestsForSupplier in Controller: " + stopWatch.getTotalTimeSeconds());
            return ResponseEntity.ok().body(requests);
        }catch(Exception e) {
            log.info("getAllRequests Error:" + e.getMessage());
            return new ResponseEntity("Error Fetching Requests", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getAllRequestsByUserId(@RequestParam(defaultValue = "0") Integer pageNo,
                                                    @RequestParam(defaultValue = "50") Integer pageSize){
        try{
            Long user = userHelper.getAuthenticatedUser();
            if(user == null) {
                return new ResponseEntity<>("User Not Found", HttpStatus.NOT_FOUND);
            }
            List<RequestVO> requests = requestService.findAllRequestsByUserId(user, pageNo, pageSize);
            if(requests == null) {
                return new ResponseEntity<>("Requests Not Found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(requests);
        }catch(Exception e) {
            log.info("getRequestsByUserId Error:" + e.getStackTrace());
            return new ResponseEntity("Error Fetching Requests", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getRequestUserStatistics(@PathVariable("userId") Long userId){
        try{
            RequestUserStatsDto request = requestService.getRequestUserStatistics(userId);
            if(request == null) {
                return new ResponseEntity<>("Requests Not Found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(request);
        }catch(Exception e) {
            log.info("getRequestsByUserId Error:" + e.getStackTrace());
            return new ResponseEntity("Error Fetching Requests", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/userHistory")
    public ResponseEntity<?> getHistoryByUserId(@RequestParam(defaultValue = "0") Integer pageNo,
                                                @RequestParam(defaultValue = "50") Integer pageSize){
        try{
            Long user = userHelper.getAuthenticatedUser();
            if(user == null) {
                return new ResponseEntity<>("User Not Found", HttpStatus.NOT_FOUND);
            }
            List<RequestVO> requests = requestService.findHistoryByUserId(user, pageNo, pageSize);
            if(requests == null) {
                return new ResponseEntity<>("Requests Not Found", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(requests);
        }catch(Exception e) {
            log.info("getRequestsHistoryByUserId Error:" + e.getMessage());
            return new ResponseEntity("Error Fetching Requests", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<?> getRequest(@PathVariable("requestId") Long requestId){
        try{
            if(!requestService.existsById(requestId)) {
                return new ResponseEntity("Request Not Found", HttpStatus.NOT_FOUND);
            }
            Request request = requestService.findRequestById(requestId);
            return ResponseEntity.ok().body(request);
        }catch(Exception e) {
            log.info("getRequest Error:" + e.getMessage());
            return new ResponseEntity("Error Fetching Request", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveRequest(@RequestParam(value = "requestBody", required = false) String stringRequest, @RequestParam(value = "subCategoryId", required = false) Long subCategoryId, @RequestPart(value = "partImages", required = false) List<MultipartFile> requestDocuments, @RequestPart(value = "carDocument", required = false) MultipartFile carFile, @RequestPart(value = "voiceNote", required = false) MultipartFile voiceNote) {
        String keyNewRequestTitle = "New Request";
        String keyNewRequestMessage = "New Request Has Been Submitted #";
        try{
            Request request = new ObjectMapper().readValue(stringRequest, Request.class);
            if(request.getTenant() == null) {
                request.setTenant(userHelper.getTenant() == null ? null : userHelper.getTenant());
            }
            request.setUser(userHelper.getAuthenticatedUser());
            Request savedRequest = requestService.saveRequest(request, subCategoryId, requestDocuments, voiceNote, carFile);
            if(savedRequest != null) {
                if(savedRequest.getJob() == null) {
                    sendPushNotification(savedRequest, keyNewRequestTitle , keyNewRequestMessage + savedRequest.getId() + ".");
                }
                return ResponseEntity.ok().body(savedRequest);
            }
            return operationCode.craftResponse("operation.request.found", HttpStatus.FOUND);
        }catch(NoSuchElementException e) {
            log.info("saveRequest Error:" + e.getMessage());
            return operationCode.craftResponse("operation.badrequest", HttpStatus.BAD_REQUEST);
        }catch(NoSuchObjectException e) {
            log.info("saveRequest Error:" + e.getMessage());
            return operationCode.craftResponse("operation.badrequest", HttpStatus.BAD_REQUEST);
        }catch (JsonMappingException e) {
            log.info("saveRequest Error:" + e.getMessage());
            return operationCode.craftResponse("operation.badrequest", HttpStatus.BAD_REQUEST);
        }catch (JsonProcessingException e) {
            log.info("saveRequest Error:" + e.getMessage());
            return operationCode.craftResponse("operation.badrequest", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateRequest(@RequestParam(value = "requestBody", required = false) String stringRequest, @RequestParam(value = "subCategoryId", required = false) Long subCategoryId, @RequestPart(value = "partImages", required = false) List<MultipartFile> requestDocuments, @RequestPart(value = "carDocument", required = false) MultipartFile carFile, @RequestPart(value = "voiceNote", required = false) MultipartFile voiceNote) {
        String keyUpdateRequestTitle = "Update Request";
        String keyUpdateRequestMessage = "Request Has Been Updated #";
        try{
            Request request = new ObjectMapper().readValue(stringRequest, Request.class);
            if(request.getTenant() == null) {
                request.setTenant(userHelper.getTenant());
            }
            request.setUpdateUserId(userHelper.getAuthenticatedUser());
            if(!requestService.existsById(request.getId())) {
                return new ResponseEntity<>(new MessageResponse("operation.request.notfound", HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
            }
            Request updatedRequest = requestService.updateRequest(request, subCategoryId, requestDocuments, voiceNote, carFile);
            if(updatedRequest != null) {
                sendPushNotification(updatedRequest, keyUpdateRequestTitle , keyUpdateRequestMessage + updatedRequest.getId() + ".");
                return ResponseEntity.ok().body(updatedRequest);
            }
            return new ResponseEntity<>(new MessageResponse("operation.request.badrequest", HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            log.info("updateRequest Error:" + e.getMessage());
            return new ResponseEntity<>(new MessageResponse("operation.request.forbidden", HttpStatus.FORBIDDEN.value()), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/cancelRequest/{requestId}")
    public ResponseEntity<?> cancelRequest(@PathVariable("requestId") Long requestId) {
        String keyCancelRequestTitle = "Request Cancellation";
        String keyCancelRequestMessage = "Request #" + requestId + " Has Been Cancelled.";
        try{
            if(!requestService.existsById(requestId)) {
                return new ResponseEntity<>("Request Not Found", HttpStatus.NOT_FOUND);
            }
            boolean canceledRequest = requestService.cancelRequest(requestId, userHelper.getAuthenticatedUser());
            if(canceledRequest) {
                sendPushNotification(requestService.findRequestById(requestId), keyCancelRequestTitle, keyCancelRequestMessage);
                return operationCode.craftResponse("operation.ok", HttpStatus.OK);
            }
            return operationCode.craftResponse("operation.request.cancel.badrequest", HttpStatus.BAD_REQUEST);
        }catch(Exception e) {
            log.info("cancelRequest Error:" + e.getMessage());
            return operationCode.craftResponse("operation.request.cancel.forbidden", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/supplierNotInterested/{requestId}")
    public ResponseEntity<?> addNotInterestedRequestToSupplier(@PathVariable("requestId") Long requestId){
        try{
            boolean exists = requestService.existsById(requestId);
            if(!exists) {
                return operationCode.craftResponse("bid.getbidbyrequestid.requestnotfound", HttpStatus.NOT_FOUND);
            }
            Long supplier = userHelper.getAuthenticatedSupplierId();
            if(supplier == null) {
                return operationCode.craftResponse("bid.gethistorybysupplier.suppliernotfound", HttpStatus.NOT_FOUND);
            }
            boolean result = supplierService.addNotInterestedSupplierToRequest(requestId, supplier);
            if(result) {
                return operationCode.craftResponse("operation.ok", HttpStatus.OK);
            }else{
                return operationCode.craftResponse("operation.badrequest", HttpStatus.BAD_REQUEST);
            }
        }catch(Exception e) {
            log.info("getAllRequests Error:" + e.getMessage());
            return operationCode.craftResponse("operation.exception", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/notInterestedSuppliers/{requestId}")
    public ResponseEntity<?> getNotInterestedSuppliersForRequest(@PathVariable("requestId") Long requestId){
        try{
            boolean exists = requestService.existsById(requestId);
            if(!exists) {
                return new ResponseEntity<>("Request Not Found", HttpStatus.NOT_FOUND);
            }
            List<Supplier> suppliers = supplierService.findNotInterestedSuppliersForRequest(requestId);
            List<SupplierDto> supplierDtos;
            if(suppliers == null) {
                return new ResponseEntity<>("There is no Not Interested Suppliers for this Request", HttpStatus.NOT_FOUND);
            }
            supplierDtos = suppliers.stream().map((Supplier s) -> new SupplierDto(s.getId(), s.getUser(), s.getName(), s.getEmail(), s.getCr(), s.getContactName(), s.getPhoneNumber(), s.getLocations() ,s.isEnabled(), bidService.countBidsBySupplier(s.getId()), bidService.countCompletedDealsBySupplier(s.getId()), 0)).collect(Collectors.toList());
            return ResponseEntity.ok().body(supplierDtos);
        }catch(Exception e) {
            log.info("getNotInterestedSuppliersForRequest Error:" + e.getMessage());
            return new ResponseEntity("Error Fetching Suppliers", HttpStatus.BAD_REQUEST);
        }
    }

    private void sendPushNotification(Request savedRequest, String keyTitle, String keyMessage) {
        try {
            NotificationEvent multiNotificationEvent = null;
            if (Privacy.Public.equals(savedRequest.getPrivacy())) {
                List<Long> supplierUsers = supplierService.findSuppliersUserIdListByBrand(savedRequest.getCar().getBrandId());
                if (supplierUsers != null) {
                    multiNotificationEvent = new NotificationEvent(PrincipleType.Supplier.name(), supplierUsers, NotificationType.Request.name(), savedRequest.getId(), keyTitle, keyMessage, "Public");
                }
            } else {
                multiNotificationEvent = new NotificationEvent(PrincipleType.Supplier.name(), savedRequest.getSuppliers().parallelStream().map(Supplier::getUser).collect(Collectors.toList()), NotificationType.Request.name(), savedRequest.getId(), keyTitle, keyMessage, "Private");
            }
            if(multiNotificationEvent != null) {
                kernelFeign.sendPushNotification(multiNotificationEvent);
//                streamBridge.send("notify-out-0", multiNotificationEvent);
            }
        } catch (Exception e) {
            log.info("Exception Inside sendPushNotification, " + e.getMessage());
        }
    }


}
