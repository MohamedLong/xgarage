package com.xgarage.app.service.serviceimpl;

import com.xgarage.app.dto.RequestUserStatsDto;
import com.xgarage.app.dto.RequestVO;
import com.xgarage.app.dto.SupplierVO;
import com.xgarage.app.model.*;
import com.xgarage.app.repository.RequestRepository;
import com.xgarage.app.service.*;
import com.xgarage.app.utils.UserHelperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.MultipartFile;

import java.rmi.NoSuchObjectException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static com.xgarage.app.utils.RequestStatusConstants.*;

@Service
@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED)
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {

    @Autowired private RequestRepository requestRepository;
    @Autowired private StatusService statusService;
    @Autowired private BidService bidService;
    @Autowired private CarService carService;
    @Autowired private PartService partService;
    @Autowired private DocumentService documentService;
    @Autowired private McqService questionService;
    @Autowired private UserHelperService userHelper;
    @Autowired private SubCategoryService subCategoryService;
    @Autowired private SupplierService supplierService;

    static Status cancelStatus = null;
    static Status completedStatus = null;


    @Override
    @Transactional(readOnly = true)
    public Request findProxyRequestById(Long id){return requestRepository.getById(id);}

    @Override
    @Transactional(readOnly = true)
    public Request findRequestById(Long id){
        Optional<Request> requestOptional = requestRepository.findById(id);
        Request request = null;
        if(requestOptional.isPresent()) {
            request = requestOptional.get();
            if (bidService.countByRequestId(request.getId()) == 0) {
                request.setSubmittedBids(0);
                request.setRejectedBids(0);
                request.setSelectedBid(null);
            } else {
                request.setSubmittedBids(bidService.countByRequestId(request.getId()));
                request.setRejectedBids(bidService.countRejectedByRequestId(request.getId()));
            }
        }
        return request;
    }

    @Override
    @Transactional(readOnly = true)
    public Request findRequestByIdWithoutStatistics(Long id){
        Optional<Request> requestOptional = requestRepository.findById(id);
        return requestOptional.orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestVO> findAllRequests(Integer pageNo, Integer pageSize){
        List<RequestVO> requestList;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try{
            Pageable page = PageRequest.of(pageNo, pageSize);
            requestList = requestRepository.findAllRequests(page);
//            calculateRequestStatistics(requestList);
            stopWatch.stop();
            log.info("Time Elapsed in findAllRequests: " + stopWatch.getTotalTimeSeconds());
        }catch(Exception e) {
            return null;
        }
        return requestList;
    }

    @Override
    public List<RequestVO> findAllClaimRequests(Integer pageNo, Integer pageSize, Long claimId) {
        List<RequestVO> requestList;
        try{
            Pageable page = PageRequest.of(pageNo, pageSize);
            requestList = requestRepository.findAllClaimRequests(page, claimId);
        }catch(Exception e) {
            return null;
        }
        return requestList;
    }

    @Override
    public List<Request> findAllJobRequests(Integer pageNo, Integer pageSize, Long jobId) {
        Pageable page = PageRequest.of(pageNo, pageSize);
        return requestRepository.findAllJobRequests(page, jobId);
    }

    @Override
    public void saveAll(List<Request> requestList) {
        requestRepository.saveAll(requestList);
    }

    @Override
    @Transactional(readOnly = true)
    public void calculateRequestStatistics(List<Request> requestList) {
        for (Request request : requestList) {
            if (bidService.countByRequestId(request.getId()) == 0) {
                request.setSubmittedBids(0);
                request.setRejectedBids(0);
                request.setSelectedBid(null);
            } else {
                request.setSubmittedBids(bidService.countByRequestId(request.getId()));
                request.setRejectedBids(bidService.countRejectedByRequestId(request.getId()));
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestVO> findAllRequestsByUserId(Long userId, Integer pageNo, Integer pageSize){
        List<RequestVO> requestList;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try{
            Pageable page = PageRequest.of(pageNo, pageSize);
            requestList = requestRepository.findAllByUserId(userId, page);
        }catch(Exception e) {
            log.info("Inside findAllRequestsByUserId" + e.getMessage());
            return null;
        }
        stopWatch.stop();
        log.info("Time Elapsed in findAllRequestsByUserId: " + stopWatch.getTotalTimeSeconds());
        return requestList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestVO> findHistoryByUserId(Long userId, Integer pageNo, Integer pageSize) {
        List<RequestVO> requestList;
        try{
            Pageable page = PageRequest.of(pageNo, pageSize);
            requestList = requestRepository.findHistoryByUserId(userId, page);
            return requestList;
        }catch(Exception e) {
            log.info("Inside findHistoryByUserId" + e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Request> findRequestPage(Pageable pageable){return requestRepository.findAll(pageable);}


    @Override
    public Request saveRequest(Request request){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        request.setSubmissionDate(timestamp);
        if(request.getUser() != null) {
            request.setUser(userHelper.getAuthenticatedUser());
        }
        if(request.getRequestType() == null) {
            request.setRequestType(RequestKind.Part);
        }
        if(request.getPrivacy() == null) {
            request.setPrivacy(Privacy.Public);
        }
        if(request.getStatus() == null) {
            request.setStatus(statusService.getStatus(1L));
        }
        if(request.getSuppliers() != null && request.getSuppliers().size() > 0){
            request.setSuppliers(request.getSuppliers().stream().map(sup -> supplierService.findSupplierById(sup.getId())).collect(Collectors.toList()));
        }
        if(request.getQty() == null) {
            request.setQty(1.0);
        }
        return requestRepository.save(request);
    }

    @Override
    public long countCompletedDealsByUser(Long userId) {
        return requestRepository.countCompletedDealsByUser(userId);
    }


    @Override
    public Request saveRequest(Request request, Long subCategoryId, List<MultipartFile> requestDocuments, MultipartFile voiceNote, MultipartFile carFile)
        throws NoSuchElementException, NoSuchObjectException {
//        try{
            log.info("Show the status of transaction in saveRequest: " + TransactionSynchronizationManager.isActualTransactionActive());
//            log.info("Now in The Server: " + new Date());
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            request.setSubmissionDate(timestamp);
            request.setBidClosingDate(timestamp);
//            setDefaultBidClosingDate(request);
            if(request.getUser() != null) {
                request.setUser(userHelper.getAuthenticatedUser());
            }
            if(request.getSuppliers() != null && request.getSuppliers().size() > 0){
                request.setSuppliers(request.getSuppliers().stream().map(sup -> supplierService.findSupplierById(sup.getId())).collect(Collectors.toList()));
            }
            if(request.getPart().getId() == null) {
                SubCategory subCategory = subCategoryService.findSubCategoryById(subCategoryId);
                if(subCategory != null) {
                    Part savedPart = partService.savePart(request.getPart());
                    if(savedPart == null) {
                        request.setPart(null);
                    }else{
                        subCategoryService.addPartToSubCategory(savedPart.getId(), subCategoryId);
                        request.setPart(savedPart);
                    }
                }else{
                    throw new NoSuchElementException("No Sub Category Exists with this Id.");
                }
            }else{
                Part fetchedPart = partService.getPartById(request.getPart().getId());
                if(fetchedPart != null) {
                    request.setPart(fetchedPart);
                }else{
                    throw new NoSuchObjectException("No Part Exist with this Id");
                }
            }
            if(requestDocuments != null) {
                request.setDocuments(requestDocuments.stream().map(documentService::saveDocument).collect(Collectors.toList()));
            }
            if(request.getQty() == null) {
                request.setQty(1.0);
            }
            if(request.getCar() != null) {
                Car dbCar = (request.getCar().getId() == null ? carService.saveFullCar(request.getCar(), carFile) : carService.findCarById(request.getCar().getId()));
                if(dbCar != null) {
                    request.setCar(dbCar);
                }else{
                    throw new NoSuchObjectException("Could not save Car");
                }
            }
            if(voiceNote != null) {
                request.setVoiceNote(documentService.saveDocument(voiceNote));
            }
            if(request.getQuestions() != null) {
                log.info("Inside saveRequest Service: ");
                List<Mcq> questions = questionService.saveAllMcqs(request.getQuestions());
                if(questions.isEmpty() ){
                    log.info("Inside saveRequest Service: Could Not Save Questions: ");
                }else{
                    request.setQuestions(questions);
                }
            }
        return requestRepository.save(request);
//        }catch (Exception ex){
//            ex.printStackTrace();
//            return null;
//        }
    }

    private void setDefaultBidClosingDate(Request request) {
        if(request.getBidClosingDate().before(request.getSubmissionDate())){
            Calendar cal = Calendar.getInstance();
            cal.setTime(request.getSubmissionDate());
            cal.add(Calendar.DATE, 3);
            request.setBidClosingDate((Timestamp) cal.getTime());
        }
    }

    @Override
    public Request updateRequest(Request request, Long subCategoryId, List<MultipartFile> requestDocuments, MultipartFile voiceNote, MultipartFile carFile)
            throws NoSuchElementException, NoSuchObjectException {
        log.info("Show the status of transaction in updateRequest: " + TransactionSynchronizationManager.isActualTransactionActive());
        if(request.getId() == null)
            return null;
        Request dbRequest = findRequestById(request.getId());
        if (request.getUser() != null) {
            dbRequest.setUser(userHelper.getAuthenticatedUser());
        }
        if(request.getSuppliers() != null && request.getSuppliers().size() > 0){
            request.setSuppliers(request.getSuppliers().stream().map(sup -> supplierService.findSupplierById(sup.getId())).collect(Collectors.toList()));
        }
        if(request.getNotInterestedSuppliers() != null)
            dbRequest.setNotInterestedSuppliers(request.getNotInterestedSuppliers());
        if (request.getPart() != null && request.getPart().getId() == null) {
            SubCategory subCategory = subCategoryService.findSubCategoryById(subCategoryId);
            if (subCategory != null) {
                Part savedPart = partService.savePart(request.getPart());
                if (savedPart == null) {
                    dbRequest.setPart(null);
                } else {
                    subCategoryService.addPartToSubCategory(savedPart.getId(), subCategoryId);
                    dbRequest.setPart(savedPart);
                }
            } else {
                throw new NoSuchElementException("No Sub Category Exists with this Id.");
            }
        } else {
            Part fetchedPart = partService.getPartById(request.getPart().getId());
            if (fetchedPart != null) {
                dbRequest.setPart(fetchedPart);
            } else {
                throw new NoSuchObjectException("No Part Exist with this Id");
            }
        }
        if (requestDocuments != null) {
            dbRequest.setDocuments(requestDocuments.stream().map(documentService::saveDocument).collect(Collectors.toList()));
        }
        if(request.getCar() != null) {
            Car dbCar = (request.getCar().getId() == null ? carService.saveFullCar(request.getCar(), carFile) : carService.findCarById(request.getCar().getId()));
            if(dbCar != null) {
                request.setCar(dbCar);
            }else{
                throw new NoSuchObjectException("Could not save Car");
            }
        }
        if (voiceNote != null) {
            dbRequest.setVoiceNote(documentService.saveDocument(voiceNote));
        }
        if(request.getPrivacy() != null)
            dbRequest.setPrivacy(request.getPrivacy());
        if(request.getRequestTitle()!=null)
            dbRequest.setRequestTitle(request.getRequestTitle());
//        if(request.getStatus()!=null)
//            dbRequest.setStatus(statusService.getStatus(request.getStatus().getId()));
        if(request.getQuestions()!=null)
            dbRequest.setQuestions(request.getQuestions());
        if(request.getPartTypes() != null)
            dbRequest.setPartTypes(request.getPartTypes());
//        if(request.getBidClosingDate()!=null)
//            dbRequest.setBidClosingDate(request.getBidClosingDate());
//        if(request.getLatitude()!=null)
//            dbRequest.setLatitude(request.getLatitude());
//        if(request.getLongitude()!=null)
//            dbRequest.setLongitude(request.getLongitude());
//        if(request.getLocationName()!=null)
//            dbRequest.setLocationName(request.getLocationName());
        if(request.getDescription()!=null)
            dbRequest.setDescription(request.getDescription());
        if(request.getQty() != null) {
            dbRequest.setQty(request.getQty());
        }
        Request persistedReq = requestRepository.save(dbRequest);
        return persistedReq;
    }

    @Override
    public boolean deleteRequestById(Long id){
        try {
            if(requestRepository.existsById(id)) {
                Request request = findRequestById(id);
                if (request.getCar() != null)
                    carService.deleteCarById(request.getCar().getId());
                if (request.getDocuments().size() > 0)
                    request.getDocuments().forEach(document -> documentService.deleteDocumentById(document.getId()));
                requestRepository.deleteById(id);
                return true;
            }else{
                return false;
            }
        }catch (Exception ex){
            ex.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    @Override
    public boolean addCarToRequest(Long carId, Long requestId){
        try {
            Car car = carService.findProxyCarById(carId);
            Request request = findProxyRequestById(requestId);
            request.setCar(car);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }
    @Override
    public boolean addPartToRequest(Long partId, Long requestId){
        try {
            Part part = partService.findProxyPartById(partId);
            Request request = findProxyRequestById(requestId);
            request.setPart(part);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    @Override
    public boolean addDocumentToRequest(Long documentId, Long requestId){
        try {
            Document document = documentService.findDocumentById(documentId);
            Request request = findProxyRequestById(requestId);
            request.getDocuments().add(document);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    @Override
    public boolean addQuestionToRequest(Long questionId, Long requestId){
        try{
            Mcq question = questionService.findProxyQuestionById(questionId);
            Request request = findRequestById(requestId);
            request.getQuestions().add(question);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }


    @Override
    public boolean cancelRequest(Long requestId, Long user) {
        cancelStatus = statusService.getStatus(CANCELED_STATUS);
        completedStatus = statusService.getStatus(COMPLETED_STATUS);
        try{
            Optional<Request> request = requestRepository.findById(requestId);
            if(request != null && request.isPresent() && !request.get().getStatus().equals(completedStatus)) {
                request.get().setUpdateUserId(user);
                request.get().setStatus(cancelStatus);
                request.get().setUpdateUserId(request.get().getUser());
                long count = bidService.countByRequestId(requestId);
                if(count == 0) {
                    return true;
                }
                if(bidService.updateAllBids(requestId, cancelStatus)) {
                    return true;
                }
            }
            return false;
        }catch(Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return false;
        }

    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long requestId) {
        return requestRepository.existsById(requestId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByUser(Long userId) {
        return requestRepository.countByUser(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestVO> findInterestedRequestsForSupplier(Long supplierId, Integer pageNo, Integer pageSize) {
        try{
            Pageable page = PageRequest.of(pageNo, pageSize);
            return requestRepository.findInterestedRequestsForSupplier(supplierId, page);
        }catch(Exception e) {
            log.info("Inside findInterestedRequestsForSupplier: " + e.getMessage());
            return null;
        }
    }


    @Override
    public List<SupplierVO> findNotInterestedSuppliersInRequest(Long requestId, Integer pageNo, Integer pageSize) {
        Pageable page = PageRequest.of(pageNo, pageSize);
        return requestRepository.findNotInterestedSuppliersInRequest(requestId, page);
    }

    @Override
    public RequestUserStatsDto getRequestUserStatistics(Long userId) {
        return new RequestUserStatsDto(requestRepository.countByUser(userId), requestRepository.countCompletedDealsByUser(userId));
    }

}
