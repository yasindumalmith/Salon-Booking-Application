package com.yas.salonservice.repository;

import com.yas.salonservice.model.Salon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalonRepository extends JpaRepository<Salon,Long> {
    Salon findByOwnerId(Long ownerId);

    @Query(
          "select s from Salon s where " +
                  "(lower(s.city) like lower(concat('%', :keyword, '%')) OR  " +
                  "lower(s.name) like lower(concat('%', :keyword, '%')) OR  " +
                  "lower(s.address) like lower(concat('%', :keyword, '%')))"
    )
    List<Salon> searchSalons(@Param("keyword") String keyword);
}
