package com.kuvyrkom.chartographer.adapter.persistence.service;

import com.kuvyrkom.chartographer.adapter.persistence.exception.ChartaNotFoundException;
import com.kuvyrkom.chartographer.adapter.persistence.repository.ChartaRepository;
import com.kuvyrkom.chartographer.domain.model.Charta;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class ChartaServiceImpl {

    private ChartaRepository chartaRepository;

    public ChartaServiceImpl(ChartaRepository chartaRepository) {
        this.chartaRepository = chartaRepository;
    }

    public Charta save(Charta charta) {
        return chartaRepository.save(charta);
    }

    public Charta findByFileUUID(String fileUUID) {
        return chartaRepository.findByFileUUID(fileUUID).orElseThrow(() -> new ChartaNotFoundException(fileUUID));
    }

    public void delete(Charta charta) {
        chartaRepository.delete(charta);
    }
}
