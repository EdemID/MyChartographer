package com.kuvyrkom.chartographer.usecase.service;

import com.kuvyrkom.chartographer.adapter.persistence.service.ChartaServiceImpl;
import com.kuvyrkom.chartographer.domain.model.Charta;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
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

    public void saveRestoredFragmentCharta(String id, int x, int y, int width, int height, MultipartFile multipartFile) {
        try {
            File chartaPath = new File(chartaService.findByFileUUID(id).getFilePath());
            BufferedImage charta = ImageIO.read(chartaPath);

            BufferedImage image = ImageIO.read(multipartFile.getInputStream());
            BufferedImage fragment = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics fragmentGraphics = fragment.getGraphics();
            /** удалить*/
            fragmentGraphics.setColor(Color.YELLOW);
            fragmentGraphics.fillRect(0, 0, width, height);
            /***/
            fragmentGraphics.drawImage(image, 0, 0, null);

            Graphics chartaOriginalGraphics = charta.getGraphics();
            chartaOriginalGraphics.drawImage(fragment, x, y, null);

            ImageIO.write(charta, "bmp", chartaPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
