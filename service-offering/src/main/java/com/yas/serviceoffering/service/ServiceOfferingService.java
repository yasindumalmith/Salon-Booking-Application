package com.yas.serviceoffering.service;

import com.yas.serviceoffering.dto.CategoryDTO;
import com.yas.serviceoffering.dto.SalonDTO;
import com.yas.serviceoffering.dto.ServiceDTO;
import com.yas.serviceoffering.model.ServiceOffering;

import java.util.List;
import java.util.Set;

public interface ServiceOfferingService {

    ServiceOffering createServiceOffering(SalonDTO salonDTO, CategoryDTO categoryDTO, ServiceDTO serviceDTO);
    ServiceOffering updateServiceOffering(Long id, ServiceOffering serviceOffering) throws Exception;

    Set<ServiceOffering> getAllServiceBySalonId(Long salonId, Long categoryId);

    Set<ServiceOffering> getServicesByIds(Set<Long> ids);

    ServiceOffering getServiceOfferingById(Long id) throws Exception;
}
