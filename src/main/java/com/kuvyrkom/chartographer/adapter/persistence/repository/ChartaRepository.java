package com.kuvyrkom.chartographer.adapter.persistence.repository;

import com.kuvyrkom.chartographer.domain.model.Charta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChartaRepository extends JpaRepository<Charta, Long> {
    Optional<Charta> findByFileUUID(String fileUUID);
}
