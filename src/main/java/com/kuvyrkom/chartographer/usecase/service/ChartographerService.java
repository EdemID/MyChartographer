package com.kuvyrkom.chartographer.usecase.service;

import com.kuvyrkom.chartographer.adapter.persistence.service.ChartaServiceImpl;
import com.kuvyrkom.chartographer.domain.model.Charta;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class ChartographerService {

    private ChartaServiceImpl chartaService;

    public ChartographerService(ChartaServiceImpl chartaService) {
        this.chartaService = chartaService;
    }

    public String createNewCharta(int width, int height) {
        String fileUUID = UUID.randomUUID().toString();
        try {
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            String filePath = "src/main/resources/graph/" + fileUUID + ".bmp";
            File output = new File(filePath);
            ImageIO.write(bufferedImage, "bmp", output);

            Charta charta = new Charta(width, height, fileUUID, filePath);
            chartaService.save(charta);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileUUID;
    }
}
