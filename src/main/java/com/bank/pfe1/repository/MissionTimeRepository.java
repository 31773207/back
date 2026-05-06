package com.bank.pfe1.repository;

import com.bank.pfe1.entity.MissionTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MissionTimeRepository extends JpaRepository<MissionTime, Long> {
    List<MissionTime> findByMissionId(Long missionId);
}