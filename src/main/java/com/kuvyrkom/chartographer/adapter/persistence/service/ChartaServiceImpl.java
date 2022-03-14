package com.kuvyrkom.chartographer.adapter.persistence.service;

import com.kuvyrkom.chartographer.adapter.persistence.repository.ChartaRepository;
import com.kuvyrkom.chartographer.adapter.restapi.exception.ChartaNotFoundException;
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
        return chartaRepository.findByFileUUID(fileUUID).orElseThrow(() -> new ChartaNotFoundException("Харта с UUID " + fileUUID + " не найдена"));
    }

    public void delete(String fileUUID) {
        Charta charta = findByFileUUID(fileUUID);
        chartaRepository.delete(charta);
    }
}
