package com.bank.pfe1.repository;

import com.bank.pfe1.entity.CartNaftal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CartNaftalRepository extends JpaRepository<CartNaftal, Long> {
    List<CartNaftal> findByDriverId(Long driverId);
    List<CartNaftal> findByActive(boolean active);
}