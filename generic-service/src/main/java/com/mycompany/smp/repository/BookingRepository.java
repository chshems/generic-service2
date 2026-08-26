package com.mycompany.smp.repository;

import com.mycompany.smp.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    // Custom finder query methods matching your consumer actor requirements
    List<BookingEntity> findByConsumerId(Long consumerId);

    // Custom finder query methods matching your service provider actor requirements
    List<BookingEntity> findByServiceId(Long serviceId);
}
