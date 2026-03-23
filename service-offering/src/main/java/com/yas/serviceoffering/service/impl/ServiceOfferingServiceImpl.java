package com.yas.serviceoffering.service.impl;

import com.yas.serviceoffering.dto.CategoryDTO;
import com.yas.serviceoffering.dto.SalonDTO;
import com.yas.serviceoffering.dto.ServiceDTO;
import com.yas.serviceoffering.model.ServiceOffering;
import com.yas.serviceoffering.repository.ServiceOfferingRepository;
import com.yas.serviceoffering.service.ServiceOfferingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceOfferingServiceImpl implements ServiceOfferingService {

    private final ServiceOfferingRepository serviceOfferingRepository;

    @Override
    public ServiceOffering createServiceOffering(SalonDTO salonDTO, CategoryDTO categoryDTO, ServiceDTO serviceDTO) {
        ServiceOffering serviceOffering = new ServiceOffering();
        serviceOffering.setSalonId(salonDTO.getId());
        serviceOffering.setCategoryId(categoryDTO.getId());
        serviceOffering.setName(serviceDTO.getName());
        serviceOffering.setDescription(serviceDTO.getDescription());
        serviceOffering.setPrice(serviceDTO.getPrice());
        serviceOffering.setId(serviceDTO.getId());
        serviceOffering.setDuration(serviceDTO.getDuration());
        serviceOffering.setImage(serviceDTO.getImage());
        serviceOfferingRepository.save(serviceOffering);
        return serviceOffering;
    }

    @Override
    public ServiceOffering updateServiceOffering(Long id, ServiceOffering serviceOffering) throws Exception {
        ServiceOffering serviceOffering1 = serviceOfferingRepository.findById(id).orElse(null);
        if (serviceOffering1 != null) {

            serviceOffering1.setName(serviceOffering.getName());
            serviceOffering1.setDescription(serviceOffering.getDescription());
            serviceOffering1.setPrice(serviceOffering.getPrice());
            serviceOffering1.setDuration(serviceOffering.getDuration());
            serviceOffering1.setImage(serviceOffering.getImage());
            return serviceOfferingRepository.save(serviceOffering1);
        }else {
            throw new Exception("Service not exist");
        }
    }

    @Override
    public Set<ServiceOffering> getAllServiceBySalonId(Long salonId, Long categoryId) {
        Set<ServiceOffering> services = serviceOfferingRepository
                .findBySalonId(salonId);

        if(categoryId!=null){
            services = services.stream().filter((service)-> service.getCategoryId() !=null && service.getCategoryId().equals(categoryId)).collect(Collectors.toSet());
        }
        return services;
    }

    @Override
    public Set<ServiceOffering> getServicesByIds(Set<Long> ids) {
        List<ServiceOffering> services= serviceOfferingRepository.findAllById(ids);
        return new HashSet<>(services);
    }

    @Override
    public ServiceOffering getServiceOfferingById(Long id) throws Exception {
        ServiceOffering serviceOffering = serviceOfferingRepository.findById(id).orElse(null);
        if(serviceOffering!=null){
            return serviceOffering;
        }else{
            throw new Exception("Service not exist");
        }
    }
}
