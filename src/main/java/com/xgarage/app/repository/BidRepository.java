package com.xgarage.app.repository;

import com.xgarage.app.dto.BidClaimVO;
import com.xgarage.app.dto.BidVO;
import com.xgarage.app.model.Bid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long>{

    @Query(value = "select * from bid where status_id not in (select id from status where name_en in ('Canceled', 'Rejected', 'Lost')) and request_id = :requestId order by id desc", nativeQuery = true)
    List<Bid> findByRequestId(Long requestId);

    @Query(value = "select b.id as bidId, b.qty as qty, b.part_name as partName, b.request_id as requestId, (select name from document where id = b.voice_note_id) as voiceNote, (select name from document where id = b.revise_voicenote_id) as reviseVoiceNote, (select GROUP_CONCAT(d.name SEPARATOR ', ') from document d, bid_images bi where d.id = bi.document_id and bi.bid_id = b.id) as bidImages, create_user as userId, (select first_name from users where id = create_user) as userFirstName, (select created_date from users where id = (select user_id from request where id = b.request_id)) as userCreatedDate, b.bid_date as bidDate, b.status_id as statusId, b.price, b.service_price as servicePrice, (select id from currency where id = b.cu_id) as cuId, b.cu_rate as cuRate, b.supplier_id as supplierId, (select name from supplier where id = b.supplier_id) as supplierName, b.deliver_days as deliverDays, (select request_title from request where id = b.request_id) as requestTitle, (select count(*) from bid where request_id = b.request_id) as submittedBids, (select count(*) from bid where status_id in (select id from status where name_en in ('Canceled', 'Rejected')) and request_id = b.request_id) as rejectedBids, b.comments, b.location, b.warranty, b.revise_comments as reviseComments, b.action_comments as actionComments, order_id as OrderId from bid b where b.id = ?1", nativeQuery = true)
    BidVO findBidById(Long bidId);

    //    @Query(value = "select * from bid where status_id not in (select id from status where name_en in ('Canceled', 'Rejected', 'Lost')) and request_id = :requestId order by id desc", nativeQuery = true)
    @Query(value = "select b.id as bidId, b.qty as qty, b.part_name as partName, b.request_id as requestId, (select name from document where id = b.voice_note_id) as voiceNote, (select name from document where id = b.revise_voicenote_id) as reviseVoiceNote, (select GROUP_CONCAT(d.name SEPARATOR ', ') from document d, bid_images bi where d.id = bi.document_id and bi.bid_id = b.id) as bidImages, create_user as userId, (select first_name from users where id = create_user) as userFirstName, (select created_date from users where id = (select user_id from request where id = b.request_id)) as userCreatedDate, b.bid_date as bidDate, b.status_id as statusId, b.price, b.service_price as servicePrice, (select id from currency where id = b.cu_id) as cuId, b.cu_rate as cuRate, b.supplier_id as supplierId, (select name from supplier where id = b.supplier_id) as supplierName, b.deliver_days as deliverDays, (select request_title from request where id = b.request_id) as requestTitle, (select count(*) from bid where request_id = b.request_id) as submittedBids, (select count(*) from bid where status_id in (select id from status where name_en in ('Canceled', 'Rejected')) and request_id = b.request_id) as rejectedBids, b.comments, b.location, b.warranty, b.revise_comments as reviseComments, b.action_comments as actionComments, order_id as OrderId from bid b where b.status_id not in (select id from status where name_en in ('Canceled', 'Rejected', 'Lost', 'Completed')) and b.request_id = :requestId order by b.id desc", nativeQuery = true)
    List<BidVO> findByRequestIdVO(Long requestId, Pageable pageable);

    @Query(value = "select count(*) from bid where request_id = :requestId", nativeQuery = true)
    Long countByRequestId(Long requestId);

    @Query(value = "select count(*) from bid where status_id in (select id from status where name_en in ('Canceled', 'Rejected')) and request_id = :requestId", nativeQuery = true)
    Long countRejectedByRequestId(Long requestId);

    @Query(value = "select * from bid where status_id not in (select id from status where name_en in ('Canceled', 'Rejected', 'Completed', 'Lost')) and supplier_id = :supplierId and request_id = :requestId order by id desc", nativeQuery = true)
//    @Query(value = "select b.id, b.request_id as requestId, (select id from users where id = (select user_id from request where id = b.request_id)) as userId, (select first_name from users where id = (select user_id from request where id = b.request_id)) as userFirstName, (select created_date from users where id = (select user_id from request where id = b.request_id)) as userCreatedDate, b.bid_date as bidDate, b.status_id as statusId, b.price, b.service_price as servicePrice,(select id from currency where id = b.cu_id) as cuId, b.cu_rate as cuRate, b.supplier_id as supplierId, (select name from supplier where id = b.supplier_id) as supplierName, b.deliver_days as deliverDays, (select request_title from request where id = b.request_id) as requestTitle, (select count(*) from bid where request_id = b.request_id) as submittedBids, (select count(*) from bid where status_id in (select id from status where name_en in ('Canceled', 'Rejected')) and request_id = b.request_id) as rejectedBids, b.comments, b.location, b.warranty, b.revise_comments as reviseComments, b.action_comments as actionComments from bid b where b.status_id not in (select id from status where name_en in ('Canceled', 'Rejected', 'Completed', 'Lost')) and b.supplier_id = :supplierId and b.request_id = :requestId order by b.id desc", nativeQuery = true)
    List<Bid> findBySupplierAndRequest(Long supplierId, Long requestId);
    @Query(value = "select count(*) from bid where status_id not in (select id from status where name_en in ('Canceled', 'Rejected', 'Completed', 'Lost')) and supplier_id = :supplierId and request_id = :requestId order by id desc", nativeQuery = true)
    Integer countBySupplierAndRequest(Long supplierId, Long requestId);

    @Query(value = "select count(*) from bid where status_id not in (select id from status where name_en in ('Canceled', 'Rejected', 'Completed', 'Lost')) and supplier_id = :supplierId", nativeQuery = true)
    Long countBySupplierId(Long supplierId);

    @Query(value = "select count(*) from bid where supplier_id = :supplierId", nativeQuery = true)
    Long countBidsBySupplierId(Long supplierId);

    @Query(value = "select count(*) from bid where status_id = (select id from status where name_en  = 'Completed') and supplier_id = :supplierId", nativeQuery = true)
    Long countCompletedDealsBySupplierId(Long supplierId);

//    @Query(value = "select * from bid where status_id not in (select id from status where name_en in ('Canceled', 'Rejected', 'Completed', 'Lost')) and supplier_id = :supplierId order by id desc", nativeQuery = true)
    @Query(value = "select b.id as bidId, b.qty as qty, b.part_name as partName, b.request_id as requestId, (select name from document where id = b.voice_note_id) as voiceNote, (select name from document where id = b.revise_voicenote_id) as reviseVoiceNote, (select GROUP_CONCAT(d.name SEPARATOR ', ') from document d, bid_images bi where d.id = bi.document_id and bi.bid_id = b.id) as bidImages, create_user as userId, (select first_name from users where id = create_user) as userFirstName, (select created_date from users where id = (select user_id from request where id = b.request_id)) as userCreatedDate, b.bid_date as bidDate, b.status_id as statusId, b.price, b.service_price as servicePrice, (select id from currency where id = b.cu_id) as cuId, b.cu_rate as cuRate, b.supplier_id as supplierId, (select name from supplier where id = b.supplier_id) as supplierName, b.deliver_days as deliverDays, (select request_title from request where id = b.request_id) as requestTitle, (select count(*) from bid where request_id = b.request_id) as submittedBids, (select count(*) from bid where status_id in (select id from status where name_en in ('Canceled', 'Rejected')) and request_id = b.request_id) as rejectedBids, b.comments, b.location, b.warranty, b.revise_comments as reviseComments, b.action_comments as actionComments from bid b where b.status_id not in (select id from status where name_en in ('Canceled', 'Rejected', 'Lost', 'Completed')) and b.supplier_id = :supplierId order by b.id desc", nativeQuery = true)
    List<BidVO> findBySupplierId(Long supplierId, Pageable pageable);

//    @Query(value = "select * from bid where status_id in (select id from status where name_en in ('Completed', 'Canceled', 'Rejected', 'Lost')) and supplier_id = :supplierId order by id desc", nativeQuery = true)
    @Query(value = "select b.id as bidId, b.qty as qty, b.part_name as partName, b.request_id as requestId, (select name from document where id = b.voice_note_id) as voiceNote, (select name from document where id = b.revise_voicenote_id) as reviseVoiceNote, create_user as userId, (select first_name from users where id = create_user) as userFirstName, (select GROUP_CONCAT(d.name SEPARATOR ', ') from document d, bid_images bi where d.id = bi.document_id and bi.bid_id = b.id) as bidImages, (select created_date from users where id = (select user_id from request where id = b.request_id)) as userCreatedDate, b.bid_date as bidDate, b.status_id as statusId, b.price, b.service_price as servicePrice, (select id from currency where id = b.cu_id) as cuId, b.cu_rate as cuRate, b.supplier_id as supplierId, (select name from supplier where id = b.supplier_id) as supplierName, b.deliver_days as deliverDays, (select request_title from request where id = b.request_id) as requestTitle, (select count(*) from bid where request_id = b.request_id) as submittedBids, (select count(*) from bid where status_id in (select id from status where name_en in ('Canceled', 'Rejected')) and request_id = b.request_id) as rejectedBids, b.comments, b.location, b.warranty, b.revise_comments as reviseComments, b.action_comments as actionComments, order_id as OrderId from bid b where b.status_id in (select id from status where name_en in ('Canceled', 'Rejected', 'Lost', 'Completed')) and b.supplier_id = :supplierId order by b.id desc", nativeQuery = true)
    List<BidVO> findHistoryBySupplierId(Long supplierId, Pageable pageable);

//    @Query(value = "select * from bid where status_id not in (select id from status where name_en in ('Completed', 'Canceled', 'Rejected', 'Lost')) order by id desc", nativeQuery = true)
//    @Query(value = "select b.id, b.request_id as requestId, (select id from users where id = (select user_id from request where id = b.request_id)) as userId, (select first_name from users where id = (select user_id from request where id = b.request_id)) as userFirstName, (select created_date from users where id = (select user_id from request where id = b.request_id)) as userCreatedDate, b.bid_date as bidDate, b.status_id as statusId, b.price, b.service_price as servicePrice,(select id from currency where id = b.cu_id) as cuId, b.cu_rate as cuRate, b.supplier_id as supplierId, (select name from supplier where id = b.supplier_id) as supplierName, b.deliver_days as deliverDays, (select request_title from request where id = b.id) as requestTitle, (select count(*) from bid where request_id = b.request_id) as submittedBids, (select count(*) from bid where status_id in (select id from status where name_en in ('Canceled', 'Rejected')) and request_id = b.request_id) as rejectedBids, b.comments, b.location, b.warranty, b.revise_comments as reviseComments, b.action_comments as actionComments from bid b where b.status_id not in (select id from status where name_en in ('Completed', 'Canceled', 'Rejected', 'Lost')) order by b.id desc", nativeQuery = true)
    @Query(value = "select b.id as bidId, b.qty as qty, b.part_name as partName, b.request_id as requestId, (select name from document where id = b.voice_note_id) as voiceNote, (select name from document where id = b.revise_voicenote_id) as reviseVoiceNote, (select GROUP_CONCAT(d.name SEPARATOR ', ') from document d, bid_images bi where d.id = bi.document_id and bi.bid_id = b.id) as bidImages, create_user as userId, (select first_name from users where id = create_user) as userFirstName, (select created_date from users where id = (select user_id from request where id = b.request_id)) as userCreatedDate, b.bid_date as bidDate, b.status_id as statusId, b.price, b.service_price as servicePrice,(select id from currency where id = b.cu_id) as cuId, b.cu_rate as cuRate, b.supplier_id as supplierId, (select name from supplier where id = b.supplier_id) as supplierName, b.deliver_days as deliverDays, (select request_title from request where id = b.request_id) as requestTitle, (select count(*) from bid where request_id = b.request_id) as submittedBids, (select count(*) from bid where status_id in (select id from status where name_en in ('Canceled', 'Rejected')) and request_id = b.request_id) as rejectedBids, b.comments, b.location, b.warranty, b.revise_comments as reviseComments, b.action_comments as actionComments, order_id as OrderId from bid b where b.status_id not in (select id from status where name_en in ('Canceled', 'Rejected', 'Lost', 'Completed')) order by b.id desc", nativeQuery = true)
    List<BidVO> findAllBids(Pageable pageable);

    @Query(value = "select * from bid where request_id = :requestId order by id desc", nativeQuery = true)
//    @Query(value = "select b.id, b.request_id as requestId, (select id from users where id = (select user_id from request where id = b.request_id)) as userId, (select first_name from users where id = (select user_id from request where id = b.request_id)) as userFirstName, (select created_date from users where id = (select user_id from request where id = b.request_id)) as userCreatedDate, b.bid_date as bidDate, b.status_id as statusId, b.price, b.service_price as servicePrice,(select id from currency where id = b.cu_id) as cuId, b.cu_rate as cuRate, b.supplier_id as supplierId, (select name from supplier where id = b.supplier_id) as supplierName, b.deliver_days as deliverDays, (select request_title from request where id = b.id) as requestTitle, (select count(*) from bid where request_id = b.request_id) as submittedBids, (select count(*) from bid where status_id in (select id from status where name_en in ('Canceled', 'Rejected')) and request_id = b.request_id) as rejectedBids, b.comments, b.location, b.warranty, b.revise_comments as reviseComments, b.action_comments as actionComments from bid b where b.request_id = :requestId order by b.id desc", nativeQuery = true)
    List<Bid> findAllByRequestId(Long requestId);

    @Query(value = "select ifnull(order_id, 0) from bid where id = :bidId", nativeQuery = true)
    Long getOrderIdFromBid(Long bidId);

    @Query(value = "select b.id as bidId, b.qty as qty, b.part_name as partName, (select part_type from part_type where id = b.part_type_id) as partType, b.request_id as requestId, vat, discount, discount_type as discountType, original_price as originalPrice, (select name from document where id = b.voice_note_id) as voiceNote, (select name from document where id = b.revise_voicenote_id) as reviseVoiceNote, (select GROUP_CONCAT(d.name SEPARATOR ', ') from document d, bid_images bi where d.id = bi.document_id and bi.bid_id = bidId) as bidImages, create_user as userId, (select first_name from users where id = create_user) as userFirstName, (select created_date from users where id = (select user_id from request where id = b.request_id)) as userCreatedDate, b.bid_date as bidDate, b.status_id as statusId, b.price, b.service_price as servicePrice,(select id from currency where id = b.cu_id) as cuId, b.cu_rate as cuRate, b.supplier_id as supplierId, (select name from supplier where id = b.supplier_id) as supplierName, b.deliver_days as deliverDays, (select request_title from request where id = b.request_id) as requestTitle, (select count(*) from bid where request_id = b.request_id) as submittedBids, (select count(*) from bid where status_id in (select id from status where name_en in ('Canceled', 'Rejected')) and request_id = b.request_id) as rejectedBids, b.comments, b.location, b.warranty, b.revise_comments as reviseComments, b.action_comments as actionComments, order_id as OrderId from bid b where b.status_id not in (select id from status where name_en in ('Canceled', 'Rejected', 'Lost', 'Completed')) and b.request_id in(select id from request where job_id = :jobId) order by b.id desc", nativeQuery = true)
    List<BidVO> findByJobIdVO(Long jobId, Pageable page);


    @Query(value = "select b.id as bidId, b.qty as qty, b.part_name as partName, (select part_type from part_type where id = b.part_type_id) as partType, b.request_id as requestId, vat, discount, discount_type as discountType, original_price as originalPrice, (select name from document where id = b.voice_note_id) as voiceNote, (select name from document where id = b.revise_voicenote_id) as reviseVoiceNote, (select GROUP_CONCAT(d.name SEPARATOR ', ') from document d, bid_images bi where d.id = bi.document_id and bi.bid_id = bidId) as bidImages, create_user as userId, (select first_name from users where id = create_user) as userFirstName, (select created_date from users where id = (select user_id from request where id = b.request_id)) as userCreatedDate, b.bid_date as bidDate, b.status_id as statusId, b.price, b.service_price as servicePrice,(select id from currency where id = b.cu_id) as cuId, b.cu_rate as cuRate, b.supplier_id as supplierId, (select name from supplier where id = b.supplier_id) as supplierName, b.deliver_days as deliverDays, (select request_title from request where id = b.request_id) as requestTitle, (select count(*) from bid where request_id = b.request_id) as submittedBids, (select count(*) from bid where status_id in (select id from status where name_en in ('Canceled', 'Rejected')) and request_id = b.request_id) as rejectedBids, b.comments, b.location, b.warranty, b.revise_comments as reviseComments, b.action_comments as actionComments, order_id as OrderId from bid b where b.status_id not in (select id from status where name_en in ('Canceled', 'Rejected', 'Lost', 'Completed')) and b.request_id in(select id from request where job_id = :jobId) and b.supplier_id = :supplierId order by b.id desc", nativeQuery = true)
    List<BidVO> findByJobIdAndSupplierVO(Long jobId, Long supplierId, Pageable page);

    @Query(value = "select b.id as bidId, b.qty as qty, b.part_name as partName, (select part_type from part_type where id = b.part_type_id) as partType, b.request_id as requestId, vat, discount, discount_type as discountType, original_price as originalPrice, (select name from document where id = b.voice_note_id) as voiceNote, (select name from document where id = b.revise_voicenote_id) as reviseVoiceNote, (select GROUP_CONCAT(d.name SEPARATOR ', ') from document d, bid_images bi where d.id = bi.document_id and bi.bid_id = bidId) as bidImages, create_user as userId, (select first_name from users where id = create_user) as userFirstName, (select created_date from users where id = (select user_id from request where id = b.request_id)) as userCreatedDate, b.bid_date as bidDate, b.status_id as statusId, b.price, b.service_price as servicePrice,(select id from currency where id = b.cu_id) as cuId, b.cu_rate as cuRate, b.supplier_id as supplierId, (select name from supplier where id = b.supplier_id) as supplierName, b.deliver_days as deliverDays, (select request_title from request where id = b.request_id) as requestTitle, (select count(*) from bid where request_id = b.request_id) as submittedBids, (select count(*) from bid where status_id in (select id from status where name_en in ('Canceled', 'Rejected')) and request_id = b.request_id) as rejectedBids, b.comments, b.location, b.warranty, b.revise_comments as reviseComments, b.action_comments as actionComments, order_id as OrderId from bid b where b.status_id not in (select id from status where name_en in ('Canceled', 'Rejected', 'Lost', 'Completed')) and b.order_id = :orderId order by b.id desc", nativeQuery = true)
    List<BidVO> findByOrderVO(Long orderId, Pageable page);

    @Query(value = "select * from bid where id in :bids", nativeQuery = true)
    List<Bid> findAllBidsOfIdIn(List<Long> bids);

    @Query(value = "select user_id from request where id = (select request_id from bid where id = :bidId)", nativeQuery = true)
    Long findRequestUserByBid(Long bidId);

    @Query(value = "select create_user from bid where id = :bidId", nativeQuery = true)
    Long findSupplierUserByBid(Long bidId);

    @Query(value = "select id from bid where order_id = :orderId", nativeQuery = true)
    List<Long> findBidIdListOfOrder(Long orderId);

    @Query(value = "select b.id as bidId, b.request_id as requestId, b.price as lumpSumPrice, (select ifnull(sum(vat),0) from claim_bid where bid_id = bidId) as vat, (select ifnull(sum(discount),0) from claim_bid where bid_id = bidId) as discount, (select ifnull(sum(original_price),0) from claim_bid where bid_id = bidId) as originalPrice, create_user as userId, (select first_name from users where id = create_user) as userFirstName, b.bid_date as bidDate, b.status_id as statusId, (select name_en from status where id = b.status_id) as statusName, (select ifnull(sum(price),0) from claim_bid where bid_id = bidId) as price, (select ifnull(sum(service_price),0) from claim_bid where bid_id = bidId) as servicePrice, b.supplier_id as supplierId, (select name from tenant where id = b.supplier_id) as supplierName, b.deliver_days as deliverDays, b.warranty, order_id as OrderId from bid b where b.status_id not in (select id from status where name_en in ('Canceled', 'Rejected', 'Lost', 'Completed')) and b.request_id = (select request_id from claim where id = :claimId) and b.supplier_id = :supplier order by b.id desc", nativeQuery = true)
    List<BidClaimVO> findByClaimIdAndSupplierVO(Long claimId, Long supplier, Pageable page);

    @Query(value = "select b.id as bidId, b.request_id as requestId, b.price as lumpSumPrice, (select ifnull(sum(vat),0) from claim_bid where bid_id = bidId) as vat, (select ifnull(sum(discount),0) from claim_bid where bid_id = bidId) as discount, (select ifnull(sum(original_price),0) from claim_bid where bid_id = bidId) as originalPrice, create_user as userId, (select first_name from users where id = create_user) as userFirstName, b.bid_date as bidDate, b.status_id as statusId, (select name_en from status where id = b.status_id) as statusName, (select ifnull(sum(price),0) from claim_bid where bid_id = bidId) as price, (select ifnull(sum(service_price),0) from claim_bid where bid_id = bidId) as servicePrice, b.supplier_id as supplierId, (select name from tenant where id = b.supplier_id) as supplierName, b.deliver_days as deliverDays, b.warranty, order_id as OrderId from bid b where b.status_id not in (select id from status where name_en in ('Canceled', 'Rejected', 'Lost', 'Completed')) and b.request_id = (select request_id from claim where id = :claimId) order by b.id desc", nativeQuery = true)
    List<BidClaimVO> findByClaimIdVO(Long claimId, Pageable page);
}