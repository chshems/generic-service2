package com.mycompany.smp.repository;

import com.mycompany.smp.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    // Query method to filter out unapproved entries for the Admin moderation queue
    List<ReviewEntity> findByApprovedFalse();

    // Query method to fetch publicly visible reviews for the marketplace explorer
    List<ReviewEntity> findByServiceIdAndApprovedTrue(Long serviceId);
}
