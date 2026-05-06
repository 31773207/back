package com.bank.pfe1.repository;

import com.bank.pfe1.entity.Wilaya;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WilayaRepository extends JpaRepository<Wilaya, Long> {
    List<Wilaya> findByActive(boolean active);
    List<Wilaya> findByNameContainingIgnoreCase(String name);
    boolean existsByCode(String code);
}