package com.mycompany.smp.controller;

import com.mycompany.smp.dto.ErrorDTO;
import com.mycompany.smp.entity.ReviewEntity;
import com.mycompany.smp.exception.BusinessException;
import com.mycompany.smp.repository.ReviewRepository;
import com.mycompany.smp.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CommonUtil commonUtil;

    // ✍️ 1. CONSUMER: SUBMIT A REVIEW (Enters review with approved = false)
    @PreAuthorize("hasRole('CONSUMER')")
    @PostMapping("/reviews")
    public ResponseEntity<?> submitReview(@RequestBody Map<String, Object> req) {
        ReviewEntity review = new ReviewEntity();
        review.setServiceId(Long.valueOf(req.get("serviceId").toString()));
        review.setRating(Integer.valueOf(req.get("rating").toString()));
        review.setReviewText((String) req.get("reviewText"));
        review.setConsumerName(commonUtil.loggedInUser().getFirstName() + " " + commonUtil.loggedInUser().getLastName());
        review.setServiceName("Marketplace Service Catalog Item");
        review.setApproved(false); // Fulfills requirement: must remain hidden until Admin handles it

        return new ResponseEntity<>(reviewRepository.save(review), HttpStatus.CREATED);
    }

    // ⚙️ 2. ADMIN PANEL: VIEW UNAPPROVED PENDING REVIEWS QUEUE
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/reviews/pending")
    public ResponseEntity<List<ReviewEntity>> getPendingReviews() {
        return new ResponseEntity<>(reviewRepository.findByApprovedFalse(), HttpStatus.OK);
    }


    // ⚙️ 3. ADMIN PANEL: APPROVE OR REJECT A REVIEW TEXT BEFORE PUBLISHING
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/reviews/{reviewId}/moderate")
    public ResponseEntity<?> moderateReview(@PathVariable Long reviewId, @RequestBody Map<String, Boolean> req) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(List.of(new ErrorDTO("NOT_FOUND", "Review index not found"))));

        boolean isApproved = req.get("approved");
        if (isApproved) {
            review.setApproved(true);
            reviewRepository.save(review);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            reviewRepository.delete(review); // If rejected, remove it completely from the system
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}
