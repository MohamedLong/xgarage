package com.xgarage.app.service;

import com.xgarage.app.dto.RequestUserStatsDto;
import com.xgarage.app.dto.RequestVO;
import com.xgarage.app.dto.SupplierVO;
import com.xgarage.app.model.Request;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.rmi.NoSuchObjectException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public interface RequestService {
    @Transactional(readOnly = true)
    Request findProxyRequestById(Long id);
    @Transactional(readOnly = true)
    Request findRequestById(Long id);
    @Transactional(readOnly = true)
    Request findRequestByIdWithoutStatistics(Long id);
    @Transactional(readOnly = true)
    List<RequestVO> findAllRequests(Integer pageNo, Integer pageSize);
    @Transactional(readOnly = true)
    void calculateRequestStatistics(List<Request> requestList);
    @Transactional(readOnly = true)
    List<RequestVO> findAllRequestsByUserId(Long userId, Integer pageNo, Integer pageSize);
    @Transactional(readOnly = true)
    List<RequestVO> findHistoryByUserId(Long userId, Integer pageNo, Integer pageSize);
    @Transactional(readOnly = true)
    Page<Request> findRequestPage(Pageable pageable);
    Request saveRequest(Request request);
    long countCompletedDealsByUser(Long userId);
    Request saveRequest(Request request, Long subCategoryId, List<MultipartFile> requestDocuments, MultipartFile voiceNote, MultipartFile carFile)
            throws NoSuchElementException, NoSuchObjectException;
    Request updateRequest(Request request, Long subCategoryId, List<MultipartFile> requestDocuments, MultipartFile voiceNote, MultipartFile carFile)
            throws NoSuchElementException, NoSuchObjectException;
    boolean deleteRequestById(Long id);
    boolean addCarToRequest(Long carId, Long requestId);
    boolean addPartToRequest(Long partId, Long requestId);
    boolean addDocumentToRequest(Long documentId, Long requestId);
    boolean addQuestionToRequest(Long questionId, Long requestId);
    boolean cancelRequest(Long requestId, Long user);
    @Transactional(readOnly = true)
    boolean existsById(Long requestId);
    @Transactional(readOnly = true)
    long countByUser(Long userId);
    @Transactional(readOnly = true)
    List<RequestVO> findInterestedRequestsForSupplier(Long supplierId, Integer pageNo, Integer pageSize);
    List<SupplierVO> findNotInterestedSuppliersInRequest(Long requestId, Integer pageNo, Integer pageSize);

    RequestUserStatsDto getRequestUserStatistics(Long userId);

    List<RequestVO> findAllClaimRequests(Integer pageNo, Integer pageSize, Long claimId);

    List<Request> findAllJobRequests(Integer pageNo, Integer pageSize, Long jobId);

    void saveAll(List<Request> requestList);
}
