package com.kuvyrkom.chartographer.adapter.persistence.service;

import com.kuvyrkom.chartographer.adapter.persistence.repository.ChartaRepository;
import com.kuvyrkom.chartographer.domain.model.Charta;
import org.springframework.stereotype.Service;

@Service
public class ChartaServiceImpl {

    private ChartaRepository chartaRepository;

    public ChartaServiceImpl(ChartaRepository chartaRepository) {
        this.chartaRepository = chartaRepository;
    }

    public Charta save(Charta charta) {
        return chartaRepository.save(charta);
    }
}
