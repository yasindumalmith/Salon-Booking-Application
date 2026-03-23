package com.yas.serviceoffering.repository;

import com.yas.serviceoffering.model.ServiceOffering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ServiceOfferingRepository extends JpaRepository<ServiceOffering,Long> {
    Set<ServiceOffering> findBySalonId(Long salonId);
}
